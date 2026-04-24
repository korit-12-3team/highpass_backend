package com.example.highpass_backend.service.board;

import com.example.highpass_backend.dto.board.CommentRequest;
import com.example.highpass_backend.dto.board.CommentResponse;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.service.notification.NotificationService;
import com.example.highpass_backend.entity.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FreeBoardRepository freeBoardRepository;
    private final StudyBoardRepository studyBoardRepository;
    private final NotificationService notificationService;

    // post
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다. "));

        Comment comment = Comment.builder()
                .user(user)
                .content(request.getContent())
                .targetId(request.getTargetId())
                .targetType(Comment.TargetType.valueOf(request.getTargetType()))
                .build();

        Comment savedComment = commentRepository.save(comment);
        
        // 알림 발송 추가 (생성된 댓글 객체를 함께 넘김)
        sendCommentNotification(user, savedComment);

        return CommentResponse.from(savedComment);
    }

    private void sendCommentNotification(User sender, Comment comment) {
        User recipient = null;
        String boardTitle = "";
        Long targetId = comment.getTargetId();
        Comment.TargetType targetType = comment.getTargetType();

        if (targetType == Comment.TargetType.STUDY) {
            StudyBoard study = studyBoardRepository.findById(targetId).orElseThrow();
            recipient = study.getUser();
            boardTitle = study.getTitle();
        } else if (targetType == Comment.TargetType.FREE) {
            FreeBoard freeBoard = freeBoardRepository.findById(targetId).orElseThrow();
            recipient = freeBoard.getUser();
            boardTitle = freeBoard.getTitle();
        }

        // 자기 자신에게는 알림을 보내지 않음
        if (recipient != null
                && !recipient.getId().equals(sender.getId())
                && recipient.isCommentNotiOn()) {
            String commentContent = comment.getContent();
            String snippet = commentContent.length() > 10 ? commentContent.substring(0, 10) + "..." : commentContent;
            
            String message = String.format("%s님이 내 게시글 [%s]에 댓글을 남겼습니다.", sender.getNickname(), boardTitle);
            notificationService.send(recipient, NotificationType.COMMENT, message, targetId, targetType.name(), snippet, sender.getNickname());
        }
    }

    // get
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTarget(Long targetId, String targetType) {
        Comment.TargetType type = Comment.TargetType.valueOf(targetType.toUpperCase());

        return commentRepository.findByTargetIdAndTargetType(targetId, type)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    // delete
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다. "));

        validateAuthor(userId, comment);

        commentRepository.delete(comment);
    }


    // update
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다. "));

        validateAuthor(userId, comment);

        comment.updateContent(request.getContent());

        return CommentResponse.from(comment);
    }

    // 권한 확인 메서드 !!
    private void validateAuthor(Long userId, Comment comment) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 댓글에 대한 권한이 없습니다.");
        }
    }
}

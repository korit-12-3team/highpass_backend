package com.example.highpass_backend.service.board;

import com.example.highpass_backend.dto.board.CommentRequest;
import com.example.highpass_backend.dto.board.CommentResponse;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.user.UserRepository;
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
        System.out.println("저장된 댓글 ID: " + savedComment.getId());
        System.out.println("댓글 작성자 닉네임: " + savedComment.getUser().getNickname());
        return CommentResponse.from(savedComment);
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

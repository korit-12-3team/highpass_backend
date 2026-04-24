package com.example.highpass_backend.service.board;

import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.board.BoardLike;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.board.BoardLikeRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import com.example.highpass_backend.service.notification.NotificationService;
import com.example.highpass_backend.entity.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final StudyBoardRepository studyBoardRepository;
    private final FreeBoardRepository freeBoardRepository;
    private final NotificationService notificationService;

    @Transactional
    public void toggleLike(Long userId, BoardLike.TargetType targetType, Long targetId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. "));

        Optional<BoardLike> existingLike = boardLikeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);

        if (existingLike.isPresent()) {
            boardLikeRepository.delete(existingLike.get());
            updateBoardLikeCount(targetType, targetId, false);
        } else {
            BoardLike boardLike = BoardLike.builder()
                    .user(user)
                    .targetType(targetType)
                    .targetId(targetId)
                    .build();

            boardLikeRepository.save(boardLike);
            updateBoardLikeCount(targetType, targetId, true);
            
            // 알림 발송 추가
            sendLikeNotification(user, targetType, targetId);
        }
    }

    private void sendLikeNotification(User sender, BoardLike.TargetType targetType, Long targetId) {
        User recipient = null;
        String boardTitle = "";

        if (targetType == BoardLike.TargetType.STUDY) {
            StudyBoard study = studyBoardRepository.findById(targetId).orElseThrow();
            recipient = study.getUser();
            boardTitle = study.getTitle();
        } else if (targetType == BoardLike.TargetType.FREE) {
            FreeBoard freeBoard = freeBoardRepository.findById(targetId).orElseThrow();
            recipient = freeBoard.getUser();
            boardTitle = freeBoard.getTitle();
        }

        // 자기 자신에게는 알림을 보내지 않음 및 알림 off일때 알림을 보내지 않음
        if (recipient != null
                && !recipient.getId().equals(sender.getId())
                && recipient.isLikeNotiOn()) {
            String message = String.format("%s님이 내 게시글 [%s]에 좋아요를 남겼습니다.", sender.getNickname(), boardTitle);
            notificationService.send(recipient, NotificationType.LIKE, message, targetId, targetType.name(), null, sender.getNickname());
        }
    }

    private void updateBoardLikeCount(BoardLike.TargetType targetType, Long targetId, boolean isIncrease) {
        if (targetType == BoardLike.TargetType.STUDY) {
            StudyBoard study = studyBoardRepository.findById(targetId).orElseThrow();
            if (isIncrease) study.increaseLikeCount();
            else study.decreaseLikeCount();

        } else if (targetType == BoardLike.TargetType.FREE) {
            FreeBoard freeBoard = freeBoardRepository.findById(targetId).orElseThrow();
            if (isIncrease) freeBoard.increaseLikeCount();
            else freeBoard.decreaseLikeCount();
        }

    }
}

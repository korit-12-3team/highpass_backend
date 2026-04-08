package com.example.highpass_backend.service.board;

import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.interaction.BoardLike;
import com.example.highpass_backend.entity.study.Study;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.interaction.BoardLikeRepository;
import com.example.highpass_backend.repository.study.StudyRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final FreeBoardRepository freeBoardRepository;

    @Transactional
    public void toggleLike(Long userId, BoardLike.TargetType targetType, Long targetId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. "));

        Optional<BoardLike> existingLike = boardLikeRepository.findByUserIdAndTargetTypeAndTargetId(targetId, targetType, targetId);

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
        }
    }

    private void updateBoardLikeCount(BoardLike.TargetType targetType, Long targetId, boolean isIncrease) {
        if (targetType == BoardLike.TargetType.STUDY) {
            Study study = studyRepository.findById(targetId).orElseThrow();
            if (isIncrease) study.increaseLikeCount();
            else study.decreaseLikeCount();

        } else if (targetType == BoardLike.TargetType.FREE) {
            FreeBoard freeBoard = freeBoardRepository.findById(targetId).orElseThrow();
            if (isIncrease) freeBoard.increaseLikeCount();
            else freeBoard.decreaseLikeCount();
        }

    }
}

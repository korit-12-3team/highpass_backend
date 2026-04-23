package com.example.highpass_backend.service.board;

import com.example.highpass_backend.dto.board.StudyBoardCreateRequest;
import com.example.highpass_backend.dto.board.StudyBoardDetailResponse;
import com.example.highpass_backend.dto.board.StudyBoardListResponse;
import com.example.highpass_backend.entity.board.BoardLike;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.BoardLikeRepository;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyBoardService {
    private final StudyBoardRepository studyBoardRepository;
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public StudyBoardDetailResponse createStudy(Long userId, StudyBoardCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        StudyBoard study = StudyBoard.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .locationName(request.locationName())
                .cert(request.cert())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .placeId(request.placeId())
                .build();

        StudyBoard savedStudy = studyBoardRepository.save(study);

        return StudyBoardDetailResponse.from(savedStudy);
    }

    @Transactional(readOnly = true)
    public List<StudyBoardListResponse> getStudyList(Long currentUserId) {
        return studyBoardRepository.findAll().stream()
                .filter(study -> study.getStatus() == null || study.getStatus() == StudyBoard.Status.VISIBLE)
                .map(study -> StudyBoardListResponse.from(study, isLikedByUser(currentUserId, study.getId())))
                .toList();
    }

    @Transactional
    public StudyBoardDetailResponse getStudy(Long studyId, Long currentUserId) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        if (study.getStatus() != null && study.getStatus() != StudyBoard.Status.VISIBLE) {
            throw new RuntimeException("Hidden or deleted study board.");
        }

        study.incrementViewCount();

        return StudyBoardDetailResponse.from(study, isLikedByUser(currentUserId, study.getId()));
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        commentRepository.deleteByTargetTypeAndTargetId(Comment.TargetType.STUDY, studyId);
        boardLikeRepository.deleteByTargetTypeAndTargetId(BoardLike.TargetType.STUDY, studyId);
        studyBoardRepository.delete(study);
    }

    @Transactional
    public StudyBoardDetailResponse updateStudy(Long studyId, StudyBoardCreateRequest request) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        study.updateStudy(
                request.title(),
                request.content(),
                request.locationName(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.placeId(),
                request.cert()
        );

        return StudyBoardDetailResponse.from(study);
    }

    private boolean isLikedByUser(Long currentUserId, Long studyId) {
        if (currentUserId == null) {
            return false;
        }

        return boardLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                currentUserId,
                BoardLike.TargetType.STUDY,
                studyId
        );
    }
}

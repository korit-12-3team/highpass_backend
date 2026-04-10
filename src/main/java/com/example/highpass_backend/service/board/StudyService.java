package com.example.highpass_backend.service.board;

import com.example.highpass_backend.dto.board.StudyCreateRequest;
import com.example.highpass_backend.dto.board.StudyDetailResponse;
import com.example.highpass_backend.dto.board.StudyListResponse;
import com.example.highpass_backend.entity.board.BoardLike;
import com.example.highpass_backend.entity.board.Study;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.BoardLikeRepository;
import com.example.highpass_backend.repository.board.StudyRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public StudyDetailResponse createStudy(Long userId, StudyCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        Study study = Study.builder()
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

        Study savedStudy = studyRepository.save(study);

        return StudyDetailResponse.from(savedStudy);
    }

    @Transactional(readOnly = true)
    public List<StudyListResponse> getStudyList(Long currentUserId) {
        return studyRepository.findAll().stream()
                .map(study -> StudyListResponse.from(study, isLikedByUser(currentUserId, study.getId())))
                .toList();
    }

    @Transactional
    public StudyDetailResponse getStudy(Long studyId, Long currentUserId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        study.incrementViewCount();

        return StudyDetailResponse.from(study, isLikedByUser(currentUserId, study.getId()));
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        studyRepository.delete(study);
    }

    @Transactional
    public StudyDetailResponse updateStudy(Long studyId, StudyCreateRequest request) {
        Study study = studyRepository.findById(studyId)
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

        return StudyDetailResponse.from(study);
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

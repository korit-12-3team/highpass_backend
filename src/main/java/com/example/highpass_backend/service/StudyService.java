package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.study.StudyCreateRequest;
import com.example.highpass_backend.dto.study.StudyDetailResponse;
import com.example.highpass_backend.dto.study.StudyListResponse;
import com.example.highpass_backend.entity.study.Study;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.study.StudyRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    // Post
    @Transactional
    public StudyDetailResponse createStudy(Long userId, StudyCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. "));

        Study study = Study.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .locationName(request.locationName())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .placeId(request.placeId())
                .build();

        Study savedStudy = studyRepository.save(study);

        return StudyDetailResponse.from(savedStudy);

    }

    // Get (다건)
    @Transactional(readOnly = true)
    public List<StudyListResponse> getStudyList() {

        return studyRepository.findAll().stream()
                .map(StudyListResponse::from)
                .toList();
    }

    // Get (단건)
    @Transactional
    public StudyDetailResponse getStudy(Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 게시글입니다. "));

        study.incrementViewCount();

        return StudyDetailResponse.from(study);
    }

    // Delete
    @Transactional
    public void deleteStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다. "));

        studyRepository.delete(study);
    }

    // Update
    @Transactional
    public StudyDetailResponse updateStudy(Long studyId, StudyCreateRequest request) {
        Study study = studyRepository.findById(studyId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다. "));

        study.updateStudy(request.title(), request.content(), request.locationName(), request.address(), request.latitude(), request.longitude(), request.placeId());

        return StudyDetailResponse.from(study);
    }
}

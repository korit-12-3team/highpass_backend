package com.example.highpass_backend.controller.board;

import com.example.highpass_backend.dto.study.StudyCreateRequest;
import com.example.highpass_backend.dto.study.StudyDetailResponse;
import com.example.highpass_backend.dto.study.StudyListResponse;
import com.example.highpass_backend.service.board.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    //  Post
    @PostMapping("/{userId}")
    public ResponseEntity<StudyDetailResponse> addStudy (@PathVariable Long userId, @RequestBody StudyCreateRequest request) {
        StudyDetailResponse studyDetailResponse = studyService.createStudy(userId, request);

        return new ResponseEntity<>(studyDetailResponse, HttpStatus.CREATED);
    }

    // Get (다건)
    @GetMapping
    public ResponseEntity<List<StudyListResponse>> getAllStudy() {

        return ResponseEntity.ok(studyService.getStudyList());
    }

    // Get (단건)
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyDetailResponse> getStudy(@PathVariable Long studyId) {
        StudyDetailResponse response = studyService.getStudy(studyId);

        return ResponseEntity.ok(response);
    }

    // Delete
    @DeleteMapping("/{studyId}")
    public ResponseEntity<Void> deleteStudy (@PathVariable Long studyId) {
        studyService.deleteStudy(studyId);

        return ResponseEntity.ok().build();
    }

    // Update
    @PatchMapping("/{studyId}")
    public ResponseEntity<StudyDetailResponse> updateStudy (@PathVariable Long studyId, @RequestBody StudyCreateRequest request) {
        StudyDetailResponse response = studyService.updateStudy(studyId, request);

        return ResponseEntity.ok(response);
    }
}

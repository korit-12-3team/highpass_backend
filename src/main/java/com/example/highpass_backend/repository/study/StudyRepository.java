package com.example.highpass_backend.repository.study;

import com.example.highpass_backend.entity.study.Study;
import com.example.highpass_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<StudyRepository> findByUser(User user);

    List<StudyRepository> findByUserId(Long userId);
}
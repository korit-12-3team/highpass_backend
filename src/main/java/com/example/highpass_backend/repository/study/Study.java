package com.example.highpass_backend.repository.study;

import com.example.highpass_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Study  extends JpaRepository<Study, Long> {

    List<Study> findByUser(User user);

    List<Study> findByUserId(Long userId);


}
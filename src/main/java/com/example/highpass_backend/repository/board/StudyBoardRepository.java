package com.example.highpass_backend.repository.board;

import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyBoardRepository extends JpaRepository<StudyBoard, Long> {

    List<StudyBoardRepository> findByUser(User user);

    List<StudyBoardRepository> findByUserId(Long userId);
}

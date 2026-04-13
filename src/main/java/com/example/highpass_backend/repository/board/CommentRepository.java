package com.example.highpass_backend.repository.board;

import com.example.highpass_backend.entity.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTargetIdAndTargetType(Long targetId, Comment.TargetType targetType);

    void deleteByTargetTypeAndTargetId(Comment.TargetType targetType, Long targetId);
}

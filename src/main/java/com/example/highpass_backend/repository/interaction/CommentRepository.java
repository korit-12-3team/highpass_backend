package com.example.highpass_backend.repository.interaction;

import com.example.highpass_backend.entity.interaction.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTargetTypeAndTargetId(Comment.TargetType targetType, Long targetId);
}

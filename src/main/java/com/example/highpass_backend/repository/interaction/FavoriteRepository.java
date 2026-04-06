package com.example.highpass_backend.repository.interaction;

import com.example.highpass_backend.entity.interaction.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<BoardLike, Long> {

    List<BoardLike> findByUserId(Long userId);

    Optional<BoardLike> findByUserIdAndTargetTypeAndTargetId(Long userId, BoardLike.TargetType targetType, Long targetId);
}
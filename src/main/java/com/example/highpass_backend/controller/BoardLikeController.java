package com.example.highpass_backend.controller;

import com.example.highpass_backend.entity.interaction.BoardLike;
import com.example.highpass_backend.service.BoardLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class BoardLikeController {
    private final BoardLikeService boardLikeService;

    @PostMapping("/{targetType}/{targetId}/{userId}")
    public ResponseEntity<Void> toggleLike (
            @PathVariable Long userId,
            @PathVariable Long targetId,
            @PathVariable BoardLike.TargetType targetType
            ) {
        boardLikeService.toggleLike(userId, targetType, targetId);

        return ResponseEntity.ok().build();
    }
}

package com.example.highpass_backend.controller.board;

import com.example.highpass_backend.dto.board.CommentRequest;
import com.example.highpass_backend.dto.board.CommentResponse;
import com.example.highpass_backend.service.board.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable String targetType,
            @PathVariable Long targetId) {

        return ResponseEntity.ok(commentService.getCommentsByTarget(targetId, targetType));
    }

    @PatchMapping("/{commentId}/{userId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @PathVariable Long userId,
            @RequestBody CommentRequest request) {

        CommentResponse response = commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}/{userId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }
}


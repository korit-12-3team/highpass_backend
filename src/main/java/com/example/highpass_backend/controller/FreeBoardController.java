package com.example.highpass_backend.controller;

import com.example.highpass_backend.dto.board.FreeBoardRequest;
import com.example.highpass_backend.dto.board.FreeBoardResponse;
import com.example.highpass_backend.service.FreeBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class FreeBoardController {
    private final FreeBoardService freeBoardService;

    // post
    @PostMapping("/{userId}")
    public ResponseEntity<FreeBoardResponse> addFreeBoard (@PathVariable Long userId, @RequestBody FreeBoardRequest request) {
        FreeBoardResponse freeBoardResponse = freeBoardService.createFreeBoard(userId, request);
        return new ResponseEntity<>(freeBoardResponse, HttpStatus.CREATED);
    }

    // get (다건)
    @GetMapping
    public ResponseEntity<List<FreeBoardResponse>> getAllBoards() {
        return ResponseEntity.ok(freeBoardService.getFreeBoardList());
    }

    // get (단건 조회)
    @GetMapping("/{freeBoardId}")
    public ResponseEntity<FreeBoardResponse> getBoard(@PathVariable Long freeBoardId) {
        FreeBoardResponse response = freeBoardService.getFreeBoard(freeBoardId);
        return ResponseEntity.ok(response);
    }

    // delete
    @DeleteMapping("/{freeBoardId}")
    public ResponseEntity<Void> deleteFreeBoard(@PathVariable Long freeBoardId) {
        freeBoardService.deleteFreeBoard(freeBoardId);

        return ResponseEntity.ok().build();

    }

    // update
    @PatchMapping("/{freeBoardId}")
    public ResponseEntity<FreeBoardResponse> updateBoard (@PathVariable Long freeBoardId, @RequestBody FreeBoardRequest request) {
        FreeBoardResponse freeBoardResponse = freeBoardService.updateFreeBoard(freeBoardId, request);

        return ResponseEntity.ok(freeBoardResponse);
    }

}

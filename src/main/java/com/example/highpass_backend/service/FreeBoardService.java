package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.board.FreeBoardRequest;
import com.example.highpass_backend.dto.board.FreeBoardResponse;
import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeBoardService {
    private final FreeBoardRepository freeBoardRepository;
    private final UserRepository userRepository;

    // post
    @Transactional
    public FreeBoardResponse createFreeBoard(Long userId, FreeBoardRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. "));

        FreeBoard freeBoard = FreeBoard.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .build();

        FreeBoard savedFreeBoard = freeBoardRepository.save(freeBoard);

        return FreeBoardResponse.from(savedFreeBoard);
    }

    // get (다건 조회)
    @Transactional(readOnly = true)
    public List<FreeBoardResponse> getFreeBoardList() {

        return freeBoardRepository.findAll().stream()
                .map(FreeBoardResponse::from)
                .toList();
    }

    // get (단건 조회)
    @Transactional
    public FreeBoardResponse getFreeBoard(Long freeBoardId) {
        FreeBoard freeBoard = freeBoardRepository.findById(freeBoardId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다. "));

        freeBoard.increaseViewCount();

        return FreeBoardResponse.from(freeBoard);
    }


    // delete
    public void deleteFreeBoard(Long freeBoardId) {
        FreeBoard freeBoard = freeBoardRepository.findById(freeBoardId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다 "));

        freeBoardRepository.delete(freeBoard);
    }

    // update
    @Transactional
    public FreeBoardResponse updateFreeBoard(Long freeBoardId, FreeBoardRequest request) {
        FreeBoard freeBoard = freeBoardRepository.findById(freeBoardId).orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다"));

        freeBoard.updateBoard(request.title(), request.content());

        return FreeBoardResponse.from(freeBoard);

    }

}

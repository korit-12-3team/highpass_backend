package com.example.highpass_backend.service.board;

import com.example.highpass_backend.dto.board.FreeBoardRequest;
import com.example.highpass_backend.dto.board.FreeBoardResponse;
import com.example.highpass_backend.entity.board.BoardLike;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.BoardLikeRepository;
import com.example.highpass_backend.repository.board.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public FreeBoardResponse createFreeBoard(Long userId, FreeBoardRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        FreeBoard freeBoard = FreeBoard.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .build();

        FreeBoard savedFreeBoard = freeBoardRepository.save(freeBoard);

        return FreeBoardResponse.from(savedFreeBoard);
    }

    @Transactional(readOnly = true)
    public List<FreeBoardResponse> getFreeBoardList(Long currentUserId) {
        return freeBoardRepository.findAll().stream()
                .map(board -> FreeBoardResponse.from(board, isLikedByUser(currentUserId, board.getId())))
                .toList();
    }

    @Transactional
    public FreeBoardResponse getFreeBoard(Long freeBoardId, Long currentUserId) {
        FreeBoard freeBoard = freeBoardRepository.findById(freeBoardId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        freeBoard.increaseViewCount();

        return FreeBoardResponse.from(freeBoard, isLikedByUser(currentUserId, freeBoard.getId()));
    }

    @Transactional
    public void deleteFreeBoard(Long freeBoardId) {
        FreeBoard freeBoard = freeBoardRepository.findById(freeBoardId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        commentRepository.deleteByTargetTypeAndTargetId(Comment.TargetType.FREE, freeBoardId);
        boardLikeRepository.deleteByTargetTypeAndTargetId(BoardLike.TargetType.FREE, freeBoardId);
        freeBoardRepository.delete(freeBoard);
    }

    @Transactional
    public FreeBoardResponse updateFreeBoard(Long freeBoardId, FreeBoardRequest request) {
        FreeBoard freeBoard = freeBoardRepository.findById(freeBoardId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        freeBoard.updateBoard(request.title(), request.content());

        return FreeBoardResponse.from(freeBoard);
    }

    private boolean isLikedByUser(Long currentUserId, Long boardId) {
        if (currentUserId == null) {
            return false;
        }

        return boardLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                currentUserId,
                BoardLike.TargetType.FREE,
                boardId
        );
    }
}

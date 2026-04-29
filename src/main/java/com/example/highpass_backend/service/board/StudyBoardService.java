package com.example.highpass_backend.service.board;

import com.example.highpass_backend.dto.board.StudyBoardCreateRequest;
import com.example.highpass_backend.dto.board.StudyBoardDetailResponse;
import com.example.highpass_backend.dto.board.StudyBoardListResponse;
import com.example.highpass_backend.entity.board.BoardLike;
import com.example.highpass_backend.entity.board.Comment;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.chat.ChatParticipant;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.BoardLikeRepository;
import com.example.highpass_backend.repository.board.CommentRepository;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyBoardService {
    private final StudyBoardRepository studyBoardRepository;
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public StudyBoardDetailResponse createStudy(Long userId, StudyBoardCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        StudyBoard study = StudyBoard.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .locationName(request.locationName())
                .cert(request.cert())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .placeId(request.placeId())
                .build();
        StudyBoard savedStudy = studyBoardRepository.save(study);

        ChatRoom savedChatRoom = null;
        if(request.createChatRoom()) {
            ChatRoom chatRoom = ChatRoom.builder()
                    .name(request.title())
                    .ownerId(userId)
                    .isApprovalRequired(true)
                    .type(ChatRoom.ChatType.GROUP)
                    .build();
            savedChatRoom = chatRoomRepository.save(chatRoom);
            chatRoom.addParticipant(user, true);
            savedStudy.setChatRoom(savedChatRoom);
        }


        return StudyBoardDetailResponse.from(
                savedStudy,
                false,
                savedChatRoom != null ? savedChatRoom.getId() : null,
                0,
                true,
                "JOINED"
        );
    }

    @Transactional(readOnly = true)
    public List<StudyBoardListResponse> getStudyList(Long currentUserId) {
        return studyBoardRepository.findAll().stream()
                .filter(study -> study.getStatus() == null || study.getStatus() == StudyBoard.Status.VISIBLE)
                .map(study -> StudyBoardListResponse.from(study, isLikedByUser(currentUserId, study.getId())))
                .toList();
    }

    @Transactional
    public StudyBoardDetailResponse getStudy(Long studyId, Long currentUserId) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        if (study.getStatus() != null && study.getStatus() != StudyBoard.Status.VISIBLE) {
            throw new RuntimeException("Hidden or deleted study board.");
        }

        study.incrementViewCount();

        ChatRoom room = study.getChatRoom();
        Long chatRoomId = null;
        long currentParticipants = 0;
        boolean isParticipant = false;
        String participantStatus = "NONE";

        if (room != null) {
            chatRoomId = room.getId();
            currentParticipants = room.getParticipants().stream()
                    .filter(p -> p.getStatus() == ChatParticipant.ParticipantStatus.JOINED)
                    .count();

            if (currentUserId != null) {
                Optional<ChatParticipant> participantOpt = room.getParticipants().stream()
                        .filter(p -> p.getUser().getId().equals(currentUserId))
                        .findFirst();

                if (participantOpt.isPresent()) {
                    ChatParticipant p = participantOpt.get();
                    participantStatus = p.getStatus().name();
                    isParticipant = (p.getStatus() == ChatParticipant.ParticipantStatus.JOINED);
                }
            }
        }

        return StudyBoardDetailResponse.from(
                study,
                isLikedByUser(currentUserId, study.getId()),
                chatRoomId,
                currentParticipants,
                isParticipant,
                participantStatus
        );
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        commentRepository.deleteByTargetTypeAndTargetId(Comment.TargetType.STUDY, studyId);
        boardLikeRepository.deleteByTargetTypeAndTargetId(BoardLike.TargetType.STUDY, studyId);
        studyBoardRepository.delete(study);
    }

    @Transactional
    public StudyBoardDetailResponse updateStudy(Long studyId, StudyBoardCreateRequest request) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));

        study.updateStudy(
                request.title(),
                request.content(),
                request.locationName(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.placeId(),
                request.cert()
        );

        return StudyBoardDetailResponse.from(study);
    }

    private boolean isLikedByUser(Long currentUserId, Long studyId) {
        if (currentUserId == null) {
            return false;
        }

        return boardLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                currentUserId,
                BoardLike.TargetType.STUDY,
                studyId
        );
    }
}
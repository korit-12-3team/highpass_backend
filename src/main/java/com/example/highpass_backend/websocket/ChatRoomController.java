package com.example.highpass_backend.websocket;
import com.example.highpass_backend.dto.chat.ChatMessageDto;
import com.example.highpass_backend.dto.chat.ChatParticipantResponse;
import com.example.highpass_backend.dto.chat.ChatRoomResponse;
import com.example.highpass_backend.dto.chat.GroupChatCreateRequest;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @GetMapping("/rooms")
    public List<ChatRoomResponse> getRooms(@RequestParam(value = "userId", required = false) Long userId) {
        if (userId == null) return Collections.emptyList();
        return chatService.getMyRooms(userId);
    }

    @PostMapping("/room")
    public ChatRoomResponse createRoom(@RequestParam(name = "userId") Long userId,
                                       @RequestParam(name = "partnerId") Long partnerId) {
        ChatRoom chatRoom = chatService.createOneToOneRoom(userId, partnerId);
        return new ChatRoomResponse(chatRoom, userId);
    }

    @GetMapping("/room/{roomId}")
    public ChatRoomResponse getRoomInfo(@PathVariable Long roomId, @RequestParam Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));
        return new ChatRoomResponse(chatRoom, userId);
    }

    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId, @RequestParam Long userId) {
        chatService.updateLastReadTime(roomId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms/{roomId}/pending")
    public ResponseEntity<List<ChatParticipantResponse>> getPendingParticipants(
            @PathVariable Long roomId,
            @RequestParam Long userId) {

        List<ChatParticipantResponse> pendingList = chatService.getPendingParticipants(roomId, userId);
        return ResponseEntity.ok(pendingList);
    }

    @PostMapping("/rooms/{roomId}/approve/{targetUserId}")
    public ResponseEntity<Void> approveParticipant(
            @PathVariable Long roomId,
            @PathVariable Long targetUserId) {

        chatService.approveParticipant(roomId, targetUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rooms/{roomId}/reject/{targetUserId}")
    public ResponseEntity<Void> rejectParticipant(
            @PathVariable Long roomId,
            @PathVariable Long targetUserId) {

        chatService.rejectParticipant(roomId, targetUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/group")
    public ResponseEntity<ChatRoomResponse> createGroupRoom (@RequestBody GroupChatCreateRequest request) {
        ChatRoom groupChatRoom = chatService.createGroupChatRoom(
                request.getName(),
                request.getOwnerId(),
                Boolean.TRUE.equals(request.getApprovalRequired())
        );
        return ResponseEntity.ok(new ChatRoomResponse(groupChatRoom, request.getOwnerId()));
    }

    // 강퇴
    @DeleteMapping("/rooms/{roomId}/kick/{targetUserId}")
    public ResponseEntity<Void> kickParticipant(
            @PathVariable Long roomId,
            @PathVariable Long targetUserId,
            @RequestParam Long ownerId) {
        chatService.kickParticipant(roomId, ownerId, targetUserId);
        return ResponseEntity.ok().build();
    }

    // 나가기
    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable Long roomId,
            @RequestParam Long userId) {
        chatService.leaveRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/room/{studyId}/join")
    public ResponseEntity<Void> joinStudy(
            @PathVariable Long studyId,
            @RequestParam Long userId
    ) {
        chatService.joinStudyChat(studyId, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/rooms/{roomId}/nickname")
    public ResponseEntity<Void> updateChatRoomName(
            @PathVariable Long roomId,
            @RequestParam Long userId,
            @RequestParam String newNickname) {
        chatService.updateChatRoomName(roomId, userId, newNickname);
        return ResponseEntity.ok().build();
    }

}
package com.example.highpass_backend.websocket;
import com.example.highpass_backend.dto.chat.ChatRoomResponse;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
}
package com.example.highpass_backend.websocket;

import com.example.highpass_backend.dto.chat.ChatMessageDto;
import com.example.highpass_backend.dto.chat.ChatRoomResponse;
import com.example.highpass_backend.entity.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message) {
        if(ChatMessageDto.MessageType.ENTER.equals(message.getType())) {
            message.setMessage( message.getSenderName() + "님이 입장하셨습니다. ");
        }
        chatService.handleMessage(message);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }


}
package com.example.highpass_backend.websocket;

import com.example.highpass_backend.dto.chat.ChatMessageDto;
import com.example.highpass_backend.dto.chat.ChatRoomResponse;
import com.example.highpass_backend.entity.chat.ChatMessage;
import com.example.highpass_backend.entity.chat.ChatParticipant;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.chat.ChatMessageRepository;
import com.example.highpass_backend.repository.chat.ChatParticipantRepository;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    @Transactional
    public ChatRoom createOneToOneRoom(Long userId, Long partnerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        User partner = userRepository.findById(partnerId).orElseThrow(() -> new RuntimeException("존재하지 않는 상대방 계정입니다."));

        ChatRoom existingRoom = chatRoomRepository.findExistingChatRoom(userId, partnerId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .name("")
                            .build();
                    chatRoomRepository.save(room);

                    ChatParticipant userInfo = ChatParticipant.builder()
                            .chatRoom(room)
                            .user(user)
                            .roomNickname(partner.getNickname())
                            .build();

                    ChatParticipant partnerInfo = ChatParticipant.builder()
                            .chatRoom(room)
                            .user(partner)
                            .roomNickname(user.getNickname())
                            .build();

                    chatParticipantRepository.save(userInfo);
                    chatParticipantRepository.save(partnerInfo);

                    room.getParticipants().add(userInfo);
                    room.getParticipants().add(partnerInfo);

                    return room;
                });

        return existingRoom;
    }

    @Transactional
    public void updateRoomNickname (Long roomId, Long userId, String newNickname) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId).orElseThrow(() -> new RuntimeException("해당 채팅방 참여 정보를 찾을 수 없습니다. "));
        participant.setRoomNickname(newNickname);
    }

    @Transactional
    public void handleMessage(ChatMessageDto messageDto) {
        if (ChatMessageDto.MessageType.ENTER.equals(messageDto.getType())) {
            messageDto.setEnterMessage();
        } else if (ChatMessageDto.MessageType.TALK.equals(messageDto.getType())) {
            saveMessageToDb(messageDto);
        }

    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyRooms(Long userId) {
        return chatRoomRepository.findAllByUserId(userId).stream()
                .map(room -> new ChatRoomResponse(room, userId))
                .collect(Collectors.toList());
    }

    private void saveMessageToDb(ChatMessageDto dto) {
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getRoomId()).orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        User sender = userRepository.findById(dto.getSenderId()).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(dto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        if(!chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoom.getId(), sender.getId())) {
            ChatParticipant participant = ChatParticipant.builder()
                    .chatRoom(chatRoom)
                    .user(sender)
                    .roomNickname(sender.getNickname())
                    .build();
            chatParticipantRepository.save(participant);

        }
        messagingTemplate.convertAndSend("/subscribe/chat/room/" + dto.getRoomId(), dto);
    }

    @Transactional
    public void updateLastReadTime (Long roomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("참여 정보를 찾을 수 없습니다. "));

        participant.updateLastRead();

        chatParticipantRepository.saveAndFlush(participant);
    }

    @Transactional
    public void enterChatRoom(Long roomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참여자가 아닙니다."));

        participant.updateLastRead();
    }
}


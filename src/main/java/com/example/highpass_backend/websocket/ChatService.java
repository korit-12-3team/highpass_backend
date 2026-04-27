package com.example.highpass_backend.websocket;

import com.example.highpass_backend.dto.chat.ChatMessageDto;
import com.example.highpass_backend.dto.chat.ChatParticipantResponse;
import com.example.highpass_backend.dto.chat.ChatRoomResponse;
import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.chat.ChatMessage;
import com.example.highpass_backend.entity.chat.ChatParticipant;
import com.example.highpass_backend.entity.chat.ChatRoom;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.StudyBoardRepository;
import com.example.highpass_backend.repository.chat.ChatMessageRepository;
import com.example.highpass_backend.repository.chat.ChatParticipantRepository;
import com.example.highpass_backend.repository.chat.ChatRoomRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final StudyBoardRepository studyBoardRepository;

    @Transactional
    public ChatRoom createOneToOneRoom(Long userId, Long partnerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        User partner = userRepository.findById(partnerId).orElseThrow(() -> new RuntimeException("존재하지 않는 상대방 계정입니다."));

        ChatRoom existingRoom = chatRoomRepository.findExistingChatRoom(userId, partnerId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .name("")
                            .type(ChatRoom.ChatType.PERSONAL)
                            .isApprovalRequired(false)
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
    public ChatRoom createGroupChatRoom (String name, Long ownerId, boolean isApprovalRequired) {
        User Owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다. "));

        ChatRoom groupChatRoom = ChatRoom.builder()
                .name(name)
                .type(ChatRoom.ChatType.GROUP)
                .ownerId(ownerId)
                .isApprovalRequired(isApprovalRequired)
                .build();

        chatRoomRepository.save(groupChatRoom);

        groupChatRoom.addParticipant(Owner,true);

        return groupChatRoom;
    }

    @Transactional
    public List<ChatParticipantResponse> getPendingParticipants (Long roomId, Long ownerId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        if(!chatRoom.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        return chatParticipantRepository.findByChatRoomIdAndStatus(roomId, ChatParticipant.ParticipantStatus.PENDING)
                .stream()
                .map(ChatParticipantResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void rejectParticipant(Long roomId, Long targetUserId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, targetUserId).orElseThrow(() -> new RuntimeException("신청 내역이 없습니다 "));

        chatParticipantRepository.delete(participant);
    }

    @Transactional
    public void updateOnlineStatus(Long roomId, Long userId, boolean isOnline) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("참여 정보 없음"));

        participant.setOnline(isOnline);
    }

    @Transactional
    public void updateChatRoomName(Long roomId, Long ownerId, String newName) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        if (!room.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("방장만 이름을 변경할 수 있습니다.");
        }

        room.setName(newName);

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.NOTICE)
                        .roomId(roomId)
                        .message("채팅방 이름이 '" + newName + "'으로 변경되었습니다.")
                        .build());
    }

    @Transactional
    public void handleMessage(ChatMessageDto messageDto) {
        if (ChatMessageDto.MessageType.ENTER.equals(messageDto.getType())) {
            messageDto.setEnterMessage();
            updateLastReadTime(messageDto.getRoomId(), messageDto.getSenderId());
            messageDto.setUnreadCount(0L);
        } else if (ChatMessageDto.MessageType.TALK.equals(messageDto.getType())) {
            int unreadCount = chatParticipantRepository.countOfflineParticipants(messageDto.getRoomId(), messageDto.getSenderId());
            messageDto.setUnreadCount((long) unreadCount);

            saveMessageToDb(messageDto);
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
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
                .type(ChatMessage.MessageType.TALK)
                .build();
        chatMessageRepository.save(chatMessage);

        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .roomNickname(sender.getNickname())
                .status(ChatParticipant.ParticipantStatus.JOINED)
                .build();
    }

    @Transactional
    public void updateLastReadTime(Long roomId, Long userId) {
        try {
            ChatParticipant participant = chatParticipantRepository
                    .findByChatRoomIdAndUserId(roomId, userId)
                    .orElseThrow(() -> new RuntimeException("참여 정보를 찾을 수 없습니다."));

            participant.updateLastRead();
            chatParticipantRepository.saveAndFlush(participant);

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    ChatMessageDto.builder()
                            .type(ChatMessageDto.MessageType.READ)
                            .roomId(roomId)
                            .senderId(userId)
                            .build());
        } catch (Exception e) {
            log.warn("읽음 처리 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public void requestJoin (Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다. "));
        if (chatParticipantRepository.existsByChatRoomIdAndUserId(roomId, userId)) {
            throw new RuntimeException("이미 참여 중이거나 승인 대기 중인 방입니다.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다. "));

        chatRoom.addParticipant(user, false);

        messagingTemplate.convertAndSend("/sub/user/" + chatRoom.getOwnerId() + "/alarm", "참여 신청이 왔습니다.");

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.JOIN_REQUEST)
                        .roomId(roomId)
                        .senderId(userId)
                        .senderName(user.getNickname())
                        .build());
    }

    @Transactional
    public void approveParticipant(Long roomId, Long targetUserId) {
        ChatParticipant participant = chatParticipantRepository
                .findByChatRoomIdAndUserId(roomId, targetUserId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방 또는 사용자입니다."));

        participant.setStatus(ChatParticipant.ParticipantStatus.JOINED);

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.APPROVE)
                        .roomId(roomId)
                        .senderId(targetUserId)
                        .build());
    }

    @Transactional
    public void kickParticipant(Long roomId, Long ownerId, Long targetUserId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        if (!room.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("방장만 강퇴할 수 있습니다.");
        }

        if (targetUserId.equals(ownerId)) {
            throw new RuntimeException("방장은 강퇴할 수 없습니다.");
        }

        ChatParticipant target = chatParticipantRepository
                .findByChatRoomIdAndUserId(roomId, targetUserId)
                .orElseThrow(() -> new RuntimeException("해당 참여자가 없습니다."));

        String nickname = target.getUser().getNickname();
        chatParticipantRepository.delete(target);

        ChatMessage kickMessage = ChatMessage.builder()
                .chatRoom(room)
                .sender(null)
                .message(nickname + "님이 강퇴되었습니다.")
                .type(ChatMessage.MessageType.QUIT)
                .build();
        chatMessageRepository.save(kickMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.QUIT)
                        .roomId(roomId)
                        .senderName(nickname)
                        .message(nickname + "님이 강퇴되었습니다.")
                        .build());
    }

    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 채팅방입니다."));

        ChatParticipant participant = chatParticipantRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("참여 중인 방이 아닙니다."));

        String nickname = participant.getUser().getNickname();

        chatParticipantRepository.delete(participant);

        chatParticipantRepository.flush();

        ChatMessage leaveMessage = ChatMessage.builder()
                .chatRoom(room)
                .message(nickname + "님이 나가셨습니다.")
                .type(ChatMessage.MessageType.QUIT)
                .build();
        chatMessageRepository.save(leaveMessage);


        if (room.getOwnerId().equals(userId)) {
            List<ChatParticipant> remaining = chatParticipantRepository.findByChatRoomId(roomId);
            if (remaining.isEmpty()) {
                studyBoardRepository.findByChatRoomId(room.getId())
                        .ifPresent(study -> study.setChatRoom(null));
                chatRoomRepository.delete(room);
            } else {
                ChatParticipant newOwner = remaining.get(0);
                newOwner.setOwner(true);
                room.setOwnerId(newOwner.getUser().getId());
            }
        }

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessageDto.builder()
                        .type(ChatMessageDto.MessageType.QUIT)
                        .roomId(roomId)
                        .senderName(nickname)
                        .message(nickname + "님이 나가셨습니다.")
                        .build());
    }

    @Transactional
    public String joinStudyChat(Long studyId, Long userId) {
        StudyBoard study = studyBoardRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        if (study.getChatRoom() == null) {
            ChatRoom newRoom = ChatRoom.builder()
                    .name(study.getTitle() + " 채팅방")
                    .type(ChatRoom.ChatType.GROUP)
                    .isApprovalRequired(true)
                    .ownerId(study.getUser().getId())
                    .build();
            chatRoomRepository.save(newRoom);
            study.setChatRoom(newRoom);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));

        ChatRoom room = study.getChatRoom();

        Optional<ChatParticipant> existing = chatParticipantRepository.findByChatRoomIdAndUserId(room.getId(), userId);
        if (existing.isPresent()) {
                return existing.get().getStatus().name();
        }

        boolean isOwner = study.getUser().getId().equals(userId);

        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(room)
                .user(user)
                .isOwner(isOwner)
                .status(isOwner ? ChatParticipant.ParticipantStatus.JOINED : ChatParticipant.ParticipantStatus.PENDING)
                .build();

        chatParticipantRepository.save(participant);

        if (participant.getStatus() == ChatParticipant.ParticipantStatus.JOINED) {
            messagingTemplate.convertAndSend("/sub/chat/room/" + room.getId(),
                    user.getNickname() + "님이 입장하셨습니다.");
        }

        return participant.getStatus().name();
    }
}


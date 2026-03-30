package com.example.highpass_backend.repository.chat;

import com.example.highpass_backend.entity.chat.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByUserId(Long userId);

    List<ChatParticipant> findByChatRoomId(Long chatRoomId);
}
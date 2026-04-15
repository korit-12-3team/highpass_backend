package com.example.highpass_backend.repository.chat;

import com.example.highpass_backend.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.createdAt > :lastReadAt")
    long countUnreadMessages(@Param("roomId") Long roomId, @Param("lastReadAt") LocalDateTime lastReadAt);
}
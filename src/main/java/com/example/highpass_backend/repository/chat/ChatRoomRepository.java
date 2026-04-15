package com.example.highpass_backend.repository.chat;

import com.example.highpass_backend.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select distinct r from ChatRoom r left join fetch r.messages")
    List<ChatRoom> findAllWithMessages();

    @Query("SELECT r FROM ChatRoom r " +
            "JOIN r.participants p1 " +
            "JOIN r.participants p2 " +
            "WHERE p1.user.id = :userId " +
            "AND p2.user.id = :partnerId")
    Optional<ChatRoom> findExistingChatRoom(@Param("userId") Long userId,
                                               @Param("partnerId") Long partnerId);

    @Query("SELECT r FROM ChatRoom r " +
            "JOIN r.participants p " +
            "WHERE p.user.id = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") Long userId);
}



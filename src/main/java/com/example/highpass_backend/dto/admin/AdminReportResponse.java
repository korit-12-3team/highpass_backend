package com.example.highpass_backend.dto.admin;

import java.util.List;

public record AdminReportResponse(
        String id,
        String targetType,
        String targetId,
        String targetLabel,
        String reason,
        String reporter,
        String reporterEmail,
        String createdAt,
        String status,
        String targetUserId,
        String targetUserNickname,
        String targetUserEmail,
        String postId,
        String postType,
        String postTitle,
        String postContent,
        String postAuthor,
        String commentId,
        String commentContent,
        String commentAuthor,
        String commentPostType,
        String commentPostId,
        String commentPostTitle,
        String chatRoomId,
        String chatRoomName,
        String chatPartnerId,
        String chatPartnerNickname,
        String chatPartnerEmail,
        List<AdminReportChatMessageResponse> chatMessages
) {
}

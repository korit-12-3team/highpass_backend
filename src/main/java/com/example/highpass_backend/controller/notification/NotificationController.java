package com.example.highpass_backend.controller.notification;

import com.example.highpass_backend.dto.notification.NotificationResponse;
import com.example.highpass_backend.service.notification.NotificationService;
import com.example.highpass_backend.service.notification.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    // 내 알림 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    // 안읽은 알림 개수
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    // 읽음 처리
    @PatchMapping("/{alarmId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long alarmId) {
        notificationService.markAsRead(alarmId);
        return ResponseEntity.ok().build();
    }

    // 개별 삭제
    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long alarmId) {
        notificationService.deleteNotification(alarmId);
        return ResponseEntity.ok().build();
    }

    // 전체 삭제
    @DeleteMapping("/all/{userId}")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/settings/{userId}")
    public ResponseEntity<Void> updateNotificationSetting(
            @PathVariable Long userId,
            @RequestBody com.example.highpass_backend.dto.notification.NotificationSettingRequest request
    ) {
        notificationSettingService.updateNotificationSetting(userId, request);
        return ResponseEntity.ok().build();
    }
}

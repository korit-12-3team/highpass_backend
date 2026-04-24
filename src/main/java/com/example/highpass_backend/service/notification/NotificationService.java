package com.example.highpass_backend.service.notification;

import com.example.highpass_backend.dto.notification.NotificationResponse;
import com.example.highpass_backend.entity.notification.Notification;
import com.example.highpass_backend.entity.notification.NotificationType;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 알림 전송 (DB 저장 + 실시간 전송)
    @Transactional
    public void send(User recipient, NotificationType type, String message, Long targetId, String targetType, String content, String senderNickname) {
        // 중복 알림 방지 로직 (COMMENT 타입은 제외하고 다른 타입만 체크)
        if (type != NotificationType.COMMENT) {
            Optional<Notification> lastNotification = notificationRepository
                    .findFirstByRecipientIdAndSenderNicknameAndTypeAndTargetIdOrderByCreatedAtDesc(
                            recipient.getId(), senderNickname, type, targetId);

            if (lastNotification.isPresent()) {
                LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
                if (lastNotification.get().getCreatedAt().isAfter(fiveMinutesAgo)) {
                    // 최근 5분 이내에 이미 알림을 보냈다면 추가로 보내지 않음 (LIKE 등)
                    return;
                }
            }
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .message(message)
                .targetId(targetId)
                .targetType(targetType)
                .content(content)
                .senderNickname(senderNickname)
                .build();

        notificationRepository.save(notification);

        // 실시간 전송: /sub/notifications/{userId}
        messagingTemplate.convertAndSend("/sub/notifications/" + recipient.getId(), 
                                        NotificationResponse.from(notification));
    }

    // 내 알림 목록 조회
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 읽지 않은 알림 개수
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(Notification::markAsRead);
    }

    // 개별 삭제
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // 전체 삭제
    @Transactional
    public void deleteAllNotifications(Long userId) {
        notificationRepository.deleteByRecipientId(userId);
    }
}

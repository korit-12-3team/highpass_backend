package com.example.highpass_backend.service.notification;

import com.example.highpass_backend.dto.notification.NotificationSettingRequest;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final UserRepository userRepository;

    @Transactional
    public void updateNotificationSetting(Long userId, NotificationSettingRequest request) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        switch (request.getType()) {
            case COMMENT:
                user.toggleCommentNoti(request.isOn());
                break;
            case LIKE:
                user.toggleLikeNoti(request.isOn());
                break;
            default:
                throw new RuntimeException("알 수 없는 알림 종류입니다.");
        }

    }
}

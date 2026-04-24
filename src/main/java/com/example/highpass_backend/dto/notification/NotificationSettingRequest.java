package com.example.highpass_backend.dto.notification;

import com.example.highpass_backend.entity.notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingRequest {

    private NotificationType type;

    @JsonProperty("isOn")
    private boolean isOn;

}
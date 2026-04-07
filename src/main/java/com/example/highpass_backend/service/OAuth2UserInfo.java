package com.example.highpass_backend.service;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getNickname();
}

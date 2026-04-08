package com.example.highpass_backend.service.user;

import com.example.highpass_backend.dto.user.UpdateUserRequest;
import com.example.highpass_backend.dto.user.UserResponse;

public interface UserService {
    UserResponse getUserById(Long userId);
    UserResponse updateUser(Long userId, UpdateUserRequest request);
}

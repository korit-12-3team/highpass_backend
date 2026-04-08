package com.example.highpass_backend.controller.user;

import com.example.highpass_backend.dto.user.UpdateUserRequest;
import com.example.highpass_backend.dto.user.UserResponse;
import com.example.highpass_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }
}
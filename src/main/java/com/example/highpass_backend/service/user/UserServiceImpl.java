package com.example.highpass_backend.service.user;

import com.example.highpass_backend.dto.user.UpdatePasswordRequest;
import com.example.highpass_backend.dto.user.UpdateUserRequest;
import com.example.highpass_backend.dto.user.UserResponse;
import com.example.highpass_backend.dto.user.VerifyPasswordRequest;
import com.example.highpass_backend.entity.auth.OAuth2Account;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.auth.OAuth2AccountRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPresenceService userPresenceService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            validateCurrentPassword(user, request.getCurrentPassword());
        }

        user.updateProfile(
                request.getNickname(),
                request.getAgeRange(),
                request.getGender(),
                request.getSiDo(),
                request.getGunGu()
        );

        return toUserResponse(user);
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String newPassword = request.getNewPassword();

        validateCurrentPassword(user, request.getCurrentPassword());

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("새 비밀번호를 입력해 주세요.");
        }

        user.encodePassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public void verifyPassword(Long userId, VerifyPasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        validateCurrentPassword(user, request.getCurrentPassword());
    }

    @Override
    public void withdrawUser(Long authenticatedUserId, Long userId) {
        if (!authenticatedUserId.equals(userId)) {
            throw new IllegalArgumentException("본인 계정만 탈퇴 처리할 수 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (user.getStatus() == User.Status.DELETED) {
            return;
        }

        user.updateStatus(User.Status.DELETED);
        user.markSeen();
    }

    private void validateCurrentPassword(User user, String currentPassword) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("소셜 로그인 계정은 비밀번호 확인으로 수정할 수 없습니다.");
        }

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해 주세요.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    private UserResponse toUserResponse(User user) {
        String socialProvider = oauth2AccountRepository.findAllByUserId(user.getId()).stream()
                .findFirst()
                .map(OAuth2Account::getProvider)
                .map(Enum::name)
                .orElse(null);

        return UserResponse.from(user, socialProvider, userPresenceService.isOnline(user.getId()));
    }
}

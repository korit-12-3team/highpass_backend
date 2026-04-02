package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.Login.AuthResponseDto;
import com.example.highpass_backend.dto.Login.LoginRequestDto;
import com.example.highpass_backend.dto.Login.SignupRequestDto;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDto signup(SignupRequestDto dto) {
        if(userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = User.createLocalUser(dto.getEmail(), encodedPassword, dto.getName());
        userRepository.save(user);

        String role = "ROLE_USER";
        String token = jwtService.generateToken(user.getEmail(), role);

        return  AuthResponseDto.of(token, user.getEmail(), user.getName(), role);
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalStateException("이메일 혹은 비밀번호가 잘못되었습니다."));

        if (user.getPassword() == null) {
            throw new IllegalArgumentException("소셜 로그인 계정입니다. 구글/카카오 로그인을 이용해주세요.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        String role = "ROLE_USER";
        String token = jwtService.generateToken(user.getEmail(), role);
        return AuthResponseDto.of(token, user.getEmail(), user.getName(), role);
    }
}

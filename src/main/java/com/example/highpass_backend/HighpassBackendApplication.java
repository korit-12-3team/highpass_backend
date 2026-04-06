package com.example.highpass_backend;

import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HighpassBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighpassBackendApplication.class, args);
    }

    // 메인 클래스 안에 이 부분을 추가해 주세요!
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, FreeBoardRepository freeBoardRepository) {
        return args -> {
            User user = new User(null, "1234@test.com", "1234", "명원바보", "명원", "30", "남 ", "부산", "사상구", null);
            User savedUser = userRepository.save(user);

            FreeBoard board1 = FreeBoard.builder()
                    .user(savedUser)
                    .title("테스트 1")
                    .content("테스트 중이에용 ")
                    .viewCount(0)
                    .likeCount(0)
                    .build();

            FreeBoard board2 = FreeBoard.builder()
                    .user(savedUser)
                    .title("테스트 2")
                    .content("테스트 중입니다 222")
                    .viewCount(0)
                    .likeCount(0)
                    .build();

            freeBoardRepository.save(board1);
            freeBoardRepository.save(board2);

        };

    }

}

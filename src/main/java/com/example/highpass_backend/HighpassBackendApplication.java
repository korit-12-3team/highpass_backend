package com.example.highpass_backend;

import com.example.highpass_backend.entity.board.FreeBoard;
import com.example.highpass_backend.entity.study.Study;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.board.FreeBoardRepository;
import com.example.highpass_backend.repository.study.StudyRepository;
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

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, FreeBoardRepository freeBoardRepository, StudyRepository studyRepository) {
        return args -> {
            User user = new User(null, "1234@test.com", "1234", "명원바보", "30", "남", "부산시 ", "사상구",  null);
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

            Study study1 = Study.builder()
                    .user(savedUser)
                    .title("강남역 백엔드 모각코 인원 모집합니다!")
                    .content("매주 주말 강남역 스터디카페에 모여서 각자 코딩해요. 스프링부트 공부하시는 분 대환영입니다.")
                    .locationName("토즈 강남토즈타워점")
                    .address("서울 강남구 강남대로84길 24-4")
                    .latitude(37.4965)
                    .longitude(127.0298)
                    .placeId("123456789")
                    .build();

            studyRepository.save(study1);
        };

    }

}

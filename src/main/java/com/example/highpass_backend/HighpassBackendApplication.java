package com.example.highpass_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HighpassBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HighpassBackendApplication.class, args);
    }
}

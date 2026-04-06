package com.example.highpass_backend.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;


public record FreeBoardRequest (
    String title,
    String content
) {}
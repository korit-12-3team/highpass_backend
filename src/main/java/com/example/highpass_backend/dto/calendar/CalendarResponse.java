package com.example.highpass_backend.dto.calendar;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CalendarResponse {
    private Long id;
    private LocalDate date;
    private String title;
    private String content;
}
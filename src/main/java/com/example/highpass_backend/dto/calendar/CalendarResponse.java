package com.example.highpass_backend.dto.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
}
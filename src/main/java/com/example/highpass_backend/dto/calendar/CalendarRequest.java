package com.example.highpass_backend.dto.calendar;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CalendarRequest {
    private LocalDate date;
    private String title;
    private String content;
}
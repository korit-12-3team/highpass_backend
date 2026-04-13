package com.example.highpass_backend.dto.calendar;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CalendarRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String content;
    private String kind;
}

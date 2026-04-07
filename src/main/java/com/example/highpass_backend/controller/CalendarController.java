package com.example.highpass_backend.controller;

import com.example.highpass_backend.dto.calendar.CalendarResponse;
import com.example.highpass_backend.entity.calendar.Calendar;
import com.example.highpass_backend.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @PostMapping("/{userId}")
    public ResponseEntity<CalendarResponse> create(@PathVariable Long userId, @RequestBody Calendar request) {
        CalendarResponse calendarResponse = calendarService.createCalendar(userId, request);
        return new ResponseEntity<>(calendarResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<Calendar>> readAllCalendar() {
        return ResponseEntity.ok(calendarService.getCalendarList());
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<Void> update(@PathVariable Long userId, @RequestBody Calendar event) {
        calendarService.updateCalendar(userId, event);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        calendarService.deleteCalendar(userId);
        return ResponseEntity.ok().build();
    }
}

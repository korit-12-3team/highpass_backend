package com.example.highpass_backend.controller.calendar;

import com.example.highpass_backend.dto.calendar.CalendarResponse;
import com.example.highpass_backend.entity.calendar.Calendar;
import com.example.highpass_backend.service.calendar.CalendarService;
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
    public ResponseEntity<List<CalendarResponse>> getCalendar(@PathVariable Long userId) {
        return ResponseEntity.ok(calendarService.getCalendarList(userId));
    }

    @PatchMapping("/{calendarId}/content")
    public ResponseEntity<CalendarResponse> updateCalendar(@PathVariable Long calendarId, @RequestBody Calendar request) {
        CalendarResponse updatedCalendar = calendarService.updateCalendar(calendarId, request);
        return ResponseEntity.ok(updatedCalendar);
    }

    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> delete(@PathVariable Long calendarId) {
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok().build();
    }
}

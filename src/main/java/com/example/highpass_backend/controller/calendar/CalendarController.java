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
    
    @GetMapping("/{Id}")
    public ResponseEntity<List<Calendar>> readAllCalendar() {
        return ResponseEntity.ok(calendarService.getCalendarList());
    }

    @PatchMapping("/{Id}/content")
    public ResponseEntity<Void> update(@PathVariable Long Id, @RequestBody Calendar event) {
        calendarService.updateCalendar(Id, event);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<Void> delete(@PathVariable Long Id) {
        calendarService.deleteCalendar(Id);
        return ResponseEntity.ok().build();
    }
}

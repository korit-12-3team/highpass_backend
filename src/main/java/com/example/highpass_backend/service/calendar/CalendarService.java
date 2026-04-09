package com.example.highpass_backend.service.calendar;

import com.example.highpass_backend.dto.calendar.CalendarResponse;
import com.example.highpass_backend.entity.calendar.Calendar;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.calendar.CalendarRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    @Transactional
    public CalendarResponse createCalendar(Long userId, Calendar request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        request.setUser(user);
        Calendar saved = calendarRepository.save(request);
        return CalendarResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CalendarResponse> getCalendarList(Long userId) {
        return calendarRepository.findByUserId(userId).stream()
                .map(CalendarResponse::from)
                .toList();
    }

    @Transactional
    public CalendarResponse updateCalendar(Long calendarId, Calendar updateParam) {
        Calendar event = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("대상 일정이 없습니다."));
        event.setTitle(updateParam.getTitle());
        event.setContent(updateParam.getContent());
        event.setStartDate(updateParam.getStartDate());
        event.setEndDate(updateParam.getEndDate());
        return CalendarResponse.from(event);
    }

    @Transactional
    public void deleteCalendar(Long calendarId) {
        calendarRepository.deleteById(calendarId);
    }
}

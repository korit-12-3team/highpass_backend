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

    // 생성
    @Transactional
    public CalendarResponse createCalendar(Long userId, Calendar request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        request.setUser(user);

        Calendar saved = calendarRepository.save(request);

        return CalendarResponse.from(saved);
    }

    //  전체 조회
    @Transactional(readOnly = true)
    public List<CalendarResponse> getCalendarList() {
        return calendarRepository.findAll().stream()
                .map(CalendarResponse::from)
                .toList();
    }

    // 수정
    @Transactional
    public void updateCalendar(Long id, Calendar updateParam) {
        Calendar event = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 없습니다."));
        event.setTitle(updateParam.getTitle());
        event.setContent(updateParam.getContent());
        event.setStartDate(updateParam.getStartDate());
        event.setEndDate(updateParam.getEndDate());
    }

    // 삭제
    @Transactional
    public void deleteCalendar(Long id) {
        calendarRepository.deleteById(id);
    }
}


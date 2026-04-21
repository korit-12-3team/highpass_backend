package com.example.highpass_backend.service.calendar;

import com.example.highpass_backend.dto.calendar.CalendarResponse;
import com.example.highpass_backend.entity.calendar.Calendar;
import com.example.highpass_backend.entity.calendar.CalendarAlarmCheck;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.calendar.CalendarAlarmCheckRepository;
import com.example.highpass_backend.repository.calendar.CalendarRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final CalendarAlarmCheckRepository alarmCheckRepository;

    @Transactional
    public CalendarResponse createCalendar(Long userId, Calendar request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        request.setUser(user);
        if (request.getKind() == null || request.getKind().isBlank()) {
            request.setKind("general");
        }

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
                .orElseThrow(() -> new RuntimeException("해당 일정이 없습니다."));

        event.setTitle(updateParam.getTitle());
        event.setContent(updateParam.getContent());
        event.setStartDate(updateParam.getStartDate());
        event.setEndDate(updateParam.getEndDate() != null ? updateParam.getEndDate() : updateParam.getStartDate());
        event.setStartTime(updateParam.getStartTime());
        event.setEndTime(updateParam.getEndTime());
        event.setKind(updateParam.getKind() == null || updateParam.getKind().isBlank() ? "general" : updateParam.getKind());

        return CalendarResponse.from(event);
    }

    @Transactional
    public void deleteCalendar(Long calendarId) {
        calendarRepository.deleteById(calendarId);
    }

    //오늘 알림용 일정 목록 조회 (다시보지않음 누를시 빈리스트로 출력)
    @Transactional(readOnly = true)
    public List<CalendarResponse> getTodayAlarms(Long userId) {
        LocalDate today = LocalDate.now();

        // 1. 오늘 이미 알림을 확인했는지 확인
        Optional<CalendarAlarmCheck> alarmCheck = alarmCheckRepository.findByUserId(userId);
        if (alarmCheck.isPresent() && alarmCheck.get().getLastCheckedDate().equals(today)) {
            return List.of(); // 오늘 이미 확인했다면 아무것도 보내지 않음
        }

        // 2. 오늘 시작하거나 오늘 종료되는 일정 조회
        return calendarRepository.findTodayNotifications(userId, today).stream()
                .map(CalendarResponse::from)
                .toList();
    }

    // 알림 확인 완료 처리 (오늘 날짜로 도장 찍기)
    @Transactional
    public void markAlarmAsChecked(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        LocalDate today = LocalDate.now();

        CalendarAlarmCheck alarmCheck = alarmCheckRepository.findByUserId(userId)
                .orElseGet(() -> CalendarAlarmCheck.builder()
                        .user(user)
                        .build());

        alarmCheck.updateDate(today);
        alarmCheckRepository.save(alarmCheck);
    }
}

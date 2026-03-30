package com.example.highpass_backend.repository.calendar;

import com.example.highpass_backend.entity.calendar.Calendar;
import com.example.highpass_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByUserAndDate(User user, LocalDate date);

    List<Calendar> findByUser(User user);
}
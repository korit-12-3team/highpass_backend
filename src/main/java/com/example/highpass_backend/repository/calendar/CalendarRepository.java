package com.example.highpass_backend.repository.calendar;

import com.example.highpass_backend.entity.calendar.Calendar;
import com.example.highpass_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByUserId(User user);

}
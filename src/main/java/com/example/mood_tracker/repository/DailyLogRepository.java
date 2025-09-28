package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.DailyLog;
import com.example.mood_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByDateAndUser(LocalDate date, User user);
}

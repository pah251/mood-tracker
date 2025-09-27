package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.HourlyLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HourlyLogRepository extends JpaRepository<HourlyLog, Long> {
}

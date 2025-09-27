package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
}

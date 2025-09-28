package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findAllByOrderByTimestampDesc();

    List<MoodEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}

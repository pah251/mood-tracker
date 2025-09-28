package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.MoodEntry;
import com.example.mood_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserOrderByTimestampDesc(User user);

    List<MoodEntry> findByUserAndTimestampBetween(User user, LocalDateTime start, LocalDateTime end);
}

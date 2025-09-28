package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findAllByOrderByTimestampDesc();
}

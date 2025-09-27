package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}

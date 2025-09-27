package com.example.mood_tracker.repository;

import com.example.mood_tracker.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByNameIgnoreCase(String name);
}

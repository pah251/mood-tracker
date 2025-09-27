package com.example.mood_tracker.dto;

public record MoodEntryFormDTO(
        // Daily fields
        Double sleepHours,
        Integer sleepScore,
        Integer alcoholConsumption,
        Integer caffeineConsumption,

        // Mood entry fields
        Integer moodScore,
        String activityName,
        String description
) {
}

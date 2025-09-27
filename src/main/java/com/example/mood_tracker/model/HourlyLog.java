package com.example.mood_tracker.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HourlyLog {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private Integer moodScore;
    private String description;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "daily_log_id")
    private DailyLog dailyLog;
}

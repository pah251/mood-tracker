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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(Integer moodScore) {
        this.moodScore = moodScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public DailyLog getDailyLog() {
        return dailyLog;
    }

    public void setDailyLog(DailyLog dailyLog) {
        this.dailyLog = dailyLog;
    }
}

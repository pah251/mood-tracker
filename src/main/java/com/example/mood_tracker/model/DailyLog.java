package com.example.mood_tracker.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double sleepHours;
    private Integer sleepScore;
    private int alcoholConsumption;
    private int caffeineConsumption;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public Integer getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(Integer sleepScore) {
        this.sleepScore = sleepScore;
    }

    public int getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(int alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public int getCaffeineConsumption() {
        return caffeineConsumption;
    }

    public void setCaffeineConsumption(int caffeineConsumption) {
        this.caffeineConsumption = caffeineConsumption;
    }
}

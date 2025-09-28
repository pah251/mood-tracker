package com.example.mood_tracker.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @OneToMany
    private Set<MoodEntry> moodEntries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MoodEntry> getMoodEntries() {
        return moodEntries;
    }

    public void setMoodEntries(Set<MoodEntry> moodEntries) {
        this.moodEntries = moodEntries;
    }
}

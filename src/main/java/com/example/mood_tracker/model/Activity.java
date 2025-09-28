package com.example.mood_tracker.model;

import jakarta.persistence.*;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Activity(String name, User user)
    {
        this.name = name;
        this.user = user;
    }

    public Activity(String name)
    {
        this.name = name;
    }

    public Activity()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

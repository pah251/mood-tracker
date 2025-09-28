package com.example.mood_tracker.config;

import com.example.mood_tracker.model.User;
import com.example.mood_tracker.repository.UserRepository;
import com.example.mood_tracker.service.EmailService;
import com.example.mood_tracker.service.MoodTrackerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {
    private final UserRepository userRepository;
    private final MoodTrackerService moodTrackerService;
    private final EmailService emailService;

    public ScheduledTasks(UserRepository userRepository, MoodTrackerService moodTrackerService, EmailService emailService)
    {
        this.userRepository = userRepository;
        this.moodTrackerService = moodTrackerService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 20 * * SUN")
    public void sendWeeklyDigest()
    {
        System.out.println("Starting weekly digest email job..."); // TODO replace with LOGGER
        List<User> users = userRepository.findAll();

        for (User user : users)
        {
            String emailBody = moodTrackerService.generateAiPromptForLastWeek(user);
            emailService.sendEmail(
                    user.getEmail(),
                    "Your Weekly Mood Tracker Summary", //TODO add timestamp?
                    emailBody
            );
        }

        System.out.println("Weekly digest email job finished.");
    }

}

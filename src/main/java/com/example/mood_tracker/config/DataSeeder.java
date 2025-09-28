package com.example.mood_tracker.config;

import com.example.mood_tracker.model.Activity;
import com.example.mood_tracker.model.DailyLog;
import com.example.mood_tracker.model.MoodEntry;
import com.example.mood_tracker.repository.ActivityRepository;
import com.example.mood_tracker.repository.DailyLogRepository;
import com.example.mood_tracker.repository.MoodEntryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final MoodEntryRepository moodEntryRepository;
    private final ActivityRepository activityRepository;
    private final DailyLogRepository dailyLogRepository;
    private final Random random = new Random();

    public DataSeeder(MoodEntryRepository moodEntryRepository, ActivityRepository activityRepository, DailyLogRepository dailyLogRepository) {
        this.moodEntryRepository = moodEntryRepository;
        this.activityRepository = activityRepository;
        this.dailyLogRepository = dailyLogRepository;
    }

    @Override
    public void run(String... args) {
        // Only run if the database is empty
        if (moodEntryRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping seeder.");
            return;
        }
        System.out.println("Seeding database with dummy data...");

        List<Activity> activities = List.of(
                activityRepository.save(new Activity("Work")),
                activityRepository.save(new Activity("Study")),
                activityRepository.save(new Activity("Exercise")),
                activityRepository.save(new Activity("Free Time"))
        );

        LocalDateTime timestamp = LocalDateTime.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = timestamp.minusDays(i).toLocalDate();
            DailyLog dailyLog = dailyLogRepository.findByDate(date).orElseGet(() -> {
                DailyLog newLog = new DailyLog();
                newLog.setDate(date);
                newLog.setSleepHours(6.5 + random.nextDouble() * 2); // 6.5 to 8.5 hours
                newLog.setSleepScore(70 + random.nextInt(25)); // 70 to 95
                return dailyLogRepository.save(newLog);
            });

            for (int j = 0; j < 24; j++) {
                MoodEntry entry = new MoodEntry();
                entry.setTimestamp(timestamp.minusDays(i).withHour(j).withMinute(random.nextInt(60)));
                entry.setMoodScore(2 + random.nextInt(4)); // Mood from 2 to 5
                entry.setDescription("Auto-generated entry");
                entry.setActivity(activities.get(random.nextInt(activities.size())));
                entry.setDailyLog(dailyLog);
                moodEntryRepository.save(entry);
            }
        }
        System.out.println("Dummy data seeding complete.");
    }
}
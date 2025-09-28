package com.example.mood_tracker.config;

import com.example.mood_tracker.model.Activity;
import com.example.mood_tracker.model.DailyLog;
import com.example.mood_tracker.model.MoodEntry;
import com.example.mood_tracker.model.User;
import com.example.mood_tracker.repository.ActivityRepository;
import com.example.mood_tracker.repository.DailyLogRepository;
import com.example.mood_tracker.repository.MoodEntryRepository;
import com.example.mood_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev") // Crucially, this still only runs in your 'dev' environment
public class DataSeeder implements CommandLineRunner {

    @Value("${dev.test-user.email}")
    private String testUserEmail;

    @Value("${dev.test-user.name}")
    private String testUserName;

    private final MoodEntryRepository moodEntryRepository;
    private final ActivityRepository activityRepository;
    private final DailyLogRepository dailyLogRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public DataSeeder(MoodEntryRepository mRepo, ActivityRepository aRepo, DailyLogRepository dRepo, UserRepository uRepo) {
        this.moodEntryRepository = mRepo;
        this.activityRepository = aRepo;
        this.dailyLogRepository = dRepo;
        this.userRepository = uRepo;
    }

    @Override
    public void run(String... args) {
        // Check if any users exist. If so, assume data is seeded and skip.
        if (userRepository.count() > 0) {
            System.out.println("Database already contains users. Skipping seeder.");
            return;
        }
        System.out.println("Seeding database with dummy data for a test user...");

        // 1. Create a dummy user
        User testUser = new User();
        testUser.setEmail(testUserEmail);
        testUser.setName(testUserName);
        userRepository.save(testUser);

        // 2. Create activities for the dummy user
        List<Activity> activities = List.of(
                new Activity("Work", testUser),
                new Activity("Study", testUser),
                new Activity("Exercise", testUser),
                new Activity("Free Time", testUser)
        );
        activityRepository.saveAll(activities);

        // 3. Generate a week of data linked to the dummy user
        LocalDateTime timestamp = LocalDateTime.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = timestamp.minusDays(i).toLocalDate();
            DailyLog dailyLog = new DailyLog();
            dailyLog.setDate(date);
            dailyLog.setSleepHours(6.5 + random.nextDouble() * 2);
            dailyLog.setSleepScore(70 + random.nextInt(25));
            dailyLog.setUser(testUser); // <-- Link to user
            dailyLogRepository.save(dailyLog);

            for (int j = 0; j < 24; j++) {
                MoodEntry entry = new MoodEntry();
                entry.setTimestamp(timestamp.minusDays(i).withHour(j).withMinute(random.nextInt(60)));
                entry.setMoodScore(2 + random.nextInt(4));
                entry.setDescription("Auto-generated entry");
                entry.setActivity(activities.get(random.nextInt(activities.size())));
                entry.setDailyLog(dailyLog);
                entry.setUser(testUser); // <-- Link to user
                moodEntryRepository.save(entry);
            }
        }
        System.out.println("Dummy data seeding complete for user: " + testUserEmail);
    }
}
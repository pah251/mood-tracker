package com.example.mood_tracker.service;

import com.example.mood_tracker.dto.MoodEntryFormDTO;
import com.example.mood_tracker.model.Activity;
import com.example.mood_tracker.model.DailyLog;
import com.example.mood_tracker.model.HourlyLog;
import com.example.mood_tracker.repository.ActivityRepository;
import com.example.mood_tracker.repository.DailyLogRepository;
import com.example.mood_tracker.repository.HourlyLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MoodTrackerService {
    private final ActivityRepository activityRepository;
    private final DailyLogRepository dailyLogRepository;
    private final HourlyLogRepository hourlyLogRepository;

    public MoodTrackerService(ActivityRepository activityRepository, DailyLogRepository dailyLogRepository, HourlyLogRepository hourlyLogRepository)
    {
        this.activityRepository = activityRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.hourlyLogRepository = hourlyLogRepository;
    }

    @Transactional
    public void saveNewEntry(MoodEntryFormDTO formDTO)
    {
        // find or create the dailylog for today
        DailyLog dailyLog = dailyLogRepository.findByDate(LocalDate.now())
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setDate(LocalDate.now());
                    newLog.setSleepHours(formDTO.sleepHours());
                    newLog.setSleepScore(formDTO.moodScore());
                    newLog.setAlcoholConsumption(formDTO.alcoholConsumption());
                    newLog.setCaffeineConsumption(formDTO.caffeineConsumption());
                    return dailyLogRepository.save(newLog);
                });

        // method triggered by user input -> potentially updating the same day
        if (formDTO.sleepHours() != null) dailyLog.setSleepHours(formDTO.sleepHours());
        if (formDTO.sleepScore() != null) dailyLog.setSleepScore(formDTO.sleepScore());
        if (formDTO.alcoholConsumption() != null) dailyLog.setAlcoholConsumption(formDTO.alcoholConsumption());
        if (formDTO.caffeineConsumption() != null) dailyLog.setCaffeineConsumption(formDTO.caffeineConsumption());

        // find or create the activity
        Activity activity = activityRepository.findByNameIgnoreCase(formDTO.activityName())
                .orElseGet(() -> {
                   Activity newActivity = new Activity();
                   newActivity.setName(formDTO.activityName());
                   return activityRepository.save(newActivity);
                });

        // create and save the new MoodEntry
        HourlyLog hourlyLog = new HourlyLog();
        hourlyLog.setTimestamp(LocalDateTime.now());
        hourlyLog.setMoodScore(formDTO.moodScore());
        hourlyLog.setDescription(formDTO.description());
        hourlyLog.setActivity(activity);
        hourlyLog.setDailyLog(dailyLog);

        hourlyLogRepository.save(hourlyLog);
    }
}

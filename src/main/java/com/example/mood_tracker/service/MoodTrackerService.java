package com.example.mood_tracker.service;

import com.example.mood_tracker.dto.MoodEntryFormDTO;
import com.example.mood_tracker.model.Activity;
import com.example.mood_tracker.model.DailyLog;
import com.example.mood_tracker.model.MoodEntry;
import com.example.mood_tracker.repository.ActivityRepository;
import com.example.mood_tracker.repository.DailyLogRepository;
import com.example.mood_tracker.repository.MoodEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MoodTrackerService {
    private final ActivityRepository activityRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MoodEntryRepository moodEntryRepository;

    public MoodTrackerService(ActivityRepository activityRepository, DailyLogRepository dailyLogRepository, MoodEntryRepository moodEntryRepository)
    {
        this.activityRepository = activityRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.moodEntryRepository = moodEntryRepository;
    }

    /**
     * Function to process adding a new MoodEntry from the front-end.
     * @param formDTO DTO containing both DailyLog and MoodEntry data
     */
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
        MoodEntry moodEntry = new MoodEntry();
        moodEntry.setTimestamp(LocalDateTime.now());
        moodEntry.setMoodScore(formDTO.moodScore());
        moodEntry.setDescription(formDTO.description());
        moodEntry.setActivity(activity);
        moodEntry.setDailyLog(dailyLog);

        moodEntryRepository.save(moodEntry);
    }

    /**
     * Get all entered mood entries, sort by timestamp
     * @return List of type MoodEntry
     */
    public List<MoodEntry> findAllEntries()
    {
        return moodEntryRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Function to calculate the average mood for each day over the last week.
     * Dumb function that does only calculates from NOW (used as default in dashboard)
     * @return Map containing the average mood for each day
     */
    public Map<LocalDate, Double> getAverageMoodForLastWeek()
    {
        // create the start and end timestamps
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);

        // retrieve entries
        List<MoodEntry> entries = moodEntryRepository.findByTimestampBetween(start, end);

        // calculate the averages and return
        return entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getTimestamp().toLocalDate(),
                        Collectors.averagingInt(MoodEntry::getMoodScore)
                ));
    }
}

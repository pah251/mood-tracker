package com.example.mood_tracker.service;

import com.example.mood_tracker.dto.MoodEntryFormDTO;
import com.example.mood_tracker.model.Activity;
import com.example.mood_tracker.model.DailyLog;
import com.example.mood_tracker.model.MoodEntry;
import com.example.mood_tracker.model.User;
import com.example.mood_tracker.repository.ActivityRepository;
import com.example.mood_tracker.repository.DailyLogRepository;
import com.example.mood_tracker.repository.MoodEntryRepository;
import com.example.mood_tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MoodTrackerService {
    private final ActivityRepository activityRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final UserRepository userRepository;

    public MoodTrackerService(ActivityRepository activityRepository, DailyLogRepository dailyLogRepository, MoodEntryRepository moodEntryRepository, UserRepository userRepository)
    {
        this.activityRepository = activityRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.moodEntryRepository = moodEntryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Function to process adding a new MoodEntry from the front-end.
     * @param formDTO DTO containing both DailyLog and MoodEntry data
     */
    @Transactional
    public void saveNewEntry(MoodEntryFormDTO formDTO)
    {
        User currentUser = getCurrentUser();

        // find or create the dailylog for today
        DailyLog dailyLog = dailyLogRepository.findByDateAndUser(LocalDate.now(), currentUser)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(currentUser);
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
        Activity activity = activityRepository.findByNameIgnoreCaseAndUser(formDTO.activityName(), currentUser)
                .orElseGet(() -> {
                   Activity newActivity = new Activity();
                   newActivity.setName(formDTO.activityName());
                   newActivity.setUser(currentUser);
                   return activityRepository.save(newActivity);
                });

        // create and save the new MoodEntry
        MoodEntry moodEntry = new MoodEntry();
        moodEntry.setUser(currentUser);
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
        return moodEntryRepository.findByUserOrderByTimestampDesc(getCurrentUser());
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
        List<MoodEntry> entries = moodEntryRepository.findByUserAndTimestampBetween(getCurrentUser(), start, end);

        // calculate the averages and return
        return entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getTimestamp().toLocalDate(),
                        Collectors.averagingInt(MoodEntry::getMoodScore)
                ));
    }

    /**
     * Default function for dashboard to generate prompt suitable for any LLM to get
     * an analysis and list of recommendations on how to improve mood based on entered data.
     * @return Formatted prompt, ready for copy+paste.
     */
    public String generateAiPromptForLastWeek()
    {
        // get start and end timestamps (1 week)
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);

        List<MoodEntry> entries = moodEntryRepository.findByUserAndTimestampBetween(getCurrentUser(), start, end);

        if (entries.isEmpty())
        {
            return "Not enough data from the last 7 days. Please try again later.";
        }

        // calculate overall avg mood
        double overallAvgMood = entries.stream()
                .mapToInt(MoodEntry::getMoodScore)
                .average()
                .orElse(0.0);

        // group by activity to find best/worst
        Map<String, Double> avgMoodByActivity = entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getActivity().getName(),
                        Collectors.averagingInt(MoodEntry::getMoodScore)
                ));

        Optional<Map.Entry<String, Double>> bestActivity = avgMoodByActivity.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue));
        Optional<Map.Entry<String, Double>> worstActivity = avgMoodByActivity.entrySet().stream()
                .min(Comparator.comparing(Map.Entry::getValue));

        StringBuilder prompt = new StringBuilder();
        prompt.append("Please act as a wellness analyst. Review the following summary of my mood and activity data for the past week and provide three actionable insights for improving my overall well-being.\n\n");
        prompt.append("**Data Summary:**\n");
        prompt.append(String.format("* **Overall Average Mood:** %.1f / 5\n", overallAvgMood));
        bestActivity.ifPresent(activity -> prompt.append(String.format("* **Activity with Highest Mood:** %s (Avg: %.1f)\n", activity.getKey(), activity.getValue())));
        worstActivity.ifPresent(activity -> prompt.append(String.format("* **Activity with Lowest Mood:** %s (Avg: %.1f)\n", activity.getKey(), activity.getValue())));
        prompt.append("\nBased on this, what is the single biggest factor influencing my mood, and what is one small change I could experiment with next week?");

        return prompt.toString();
    }

    /**
     * Helper function to get the current logged-in user
     * @return User object representing the current user.
     */
    private User getCurrentUser()
    {
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = oAuth2User.getAttribute("email");

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found in Database"));
    }
}

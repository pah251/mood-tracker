package com.example.mood_tracker.controller;

import com.example.mood_tracker.dto.MoodEntryFormDTO;
import com.example.mood_tracker.model.MoodEntry;
import com.example.mood_tracker.service.MoodTrackerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MoodTrackerController {
    private final MoodTrackerService moodTrackerService;

    private static final String INDEX = "index";
    private static final String REDIRECT = "redirect:/";
    private static final String DASHBOARD = "dashboard";

    public MoodTrackerController(MoodTrackerService moodTrackerService)
    {
        this.moodTrackerService = moodTrackerService;
    }

    /**
     * GET handler
     */
    @GetMapping("/")
    public String showHomePage(Model model)
    {
        model.addAttribute(
                "moodEntryForm",
                new MoodEntryFormDTO(
                        null,
                        null,
                        null,
                        null,
                        null,
                        "",
                        "")
        );
        return INDEX;
    }

    @PostMapping("/add")
    public String handleFormSubmit(@ModelAttribute MoodEntryFormDTO moodEntryForm)
    {
        moodTrackerService.saveNewEntry(moodEntryForm);
        return REDIRECT;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model)
    {
        List<MoodEntry> entries = moodTrackerService.findAllEntries();
        model.addAttribute("entries", entries);
        return DASHBOARD;
    }
}

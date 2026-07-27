package com.example.frauddetection.controller;

import com.example.frauddetection.dto.DailyProcessingResponse;
import com.example.frauddetection.service.DailyProcessingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily-processing")
@CrossOrigin(origins = "*")
public class DailyProcessingController {

    private final DailyProcessingService dailyProcessingService;

    public DailyProcessingController(DailyProcessingService dailyProcessingService) {
        this.dailyProcessingService = dailyProcessingService;
    }

    @PostMapping
    public DailyProcessingResponse processDailyTransactions(
            @RequestParam(defaultValue = "1000") int count) {
        return dailyProcessingService.processDailyTransactions(count);
    }
}
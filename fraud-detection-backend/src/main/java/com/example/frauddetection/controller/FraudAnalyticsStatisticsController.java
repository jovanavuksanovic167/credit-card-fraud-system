package com.example.frauddetection.controller;

import com.example.frauddetection.dto.FraudAnalyticsStatisticsResponse;
import com.example.frauddetection.service.FraudAnalyticsStatisticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud-analytics-statistics")
@CrossOrigin(origins = "*")
public class FraudAnalyticsStatisticsController {

    private final FraudAnalyticsStatisticsService statisticsService;

    public FraudAnalyticsStatisticsController(FraudAnalyticsStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public FraudAnalyticsStatisticsResponse getStatistics() {
        return statisticsService.getStatistics();
    }
}
package com.example.frauddetection.service;

import com.example.frauddetection.dto.AiDailyProcessingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AI_SERVICE_URL =
            "http://localhost:8000/process-daily-transactions";

    public AiDailyProcessingResponse processDailyTransactions(int count) {
        String url = AI_SERVICE_URL + "?count=" + count;

        return restTemplate.postForObject(
                url,
                null,
                AiDailyProcessingResponse.class
        );
    }
}
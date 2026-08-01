package com.example.frauddetection.service;

import com.example.frauddetection.dto.AiDailyProcessingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class AiServiceClient {

  private final RestTemplate restTemplate;
  private final String aiServiceUrl;

  public AiServiceClient(
      @Value("${deepnetts.service.url:http://localhost:8081}") String deepNettsServiceUrl) {
    this.restTemplate = new RestTemplate();
    this.aiServiceUrl = deepNettsServiceUrl + "/process-daily-transactions";
  }

  public AiDailyProcessingResponse processDailyTransactions() {
    try {
      AiDailyProcessingResponse response =
          restTemplate.postForObject(aiServiceUrl, null, AiDailyProcessingResponse.class);

      if (response == null) {
        throw new IllegalStateException("DeepNetts servis je vratio prazan odgovor.");
      }

      return response;
    } catch (RestClientException exception) {
      throw new IllegalStateException(
          "Nije moguće povezati se sa DeepNetts servisom na adresi: " + aiServiceUrl, exception);
    }
  }
}

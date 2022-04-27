package com.sbab.assignment.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbab.assignment.exception.TrafiklabAPIClientException;
import com.sbab.assignment.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TrafiklabAPIClient {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private String jourUrl;
    private String stopUrl;

    public TrafiklabAPIClient(@Value("${trafiklab.jour.url}") String jourUrl,
                              @Value("${trafiklab.stop.url}") String stopUrl,
                              RestTemplate restTemplate,
                              ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.jourUrl = jourUrl;
        this.stopUrl = stopUrl;
    }

    @Async
    @Retryable(value = RestClientException.class, maxAttempts = 3, backoff = @Backoff(3000))
    public CompletableFuture<List<JourneyPatternPointOnLine>> getJourData() {
        try {
            log.info("Requesting JourneyPatternPointOnLine data from Trafiklab");

            ResponseEntity<String> responseEntity = restTemplate.exchange(jourUrl, HttpMethod.GET,
                    createHttpEntityWithGzipHeader(), String.class);

            JSONObject json = new JSONObject(responseEntity.getBody());
            JSONArray resultArray = json.getJSONObject("ResponseData").getJSONArray("Result");

            return CompletableFuture.completedFuture(Arrays.asList((objectMapper.readValue(resultArray.toString(),
                    JourneyPatternPointOnLine[].class))));
        } catch (RestClientException rce) {
            throw new TrafiklabAPIClientException("Error when requesting JourneyPatternPointOnLine data", rce);
        } catch (JsonProcessingException jpe) {
            throw new TrafiklabAPIClientException("Failed to deserialize JourneyPatternPointOnLine response", jpe);
        }
    }

    @Async
    @Retryable(value = RestClientException.class, maxAttempts = 3, backoff = @Backoff(3000))
    public CompletableFuture<List<StopPoint>> getStopPointData() {
        try {
            log.info("Requesting StopPoint data from Trafiklab");

            ResponseEntity<String> responseEntity = restTemplate.exchange(stopUrl, HttpMethod.GET,
                    createHttpEntityWithGzipHeader(), String.class);

            JSONObject json = new JSONObject(responseEntity.getBody());
            JSONArray resultArray = json.getJSONObject("ResponseData").getJSONArray("Result");

            return CompletableFuture.completedFuture(Arrays.asList((objectMapper.readValue(resultArray.toString(),
                    StopPoint[].class))));
        } catch (RestClientException rce) {
            throw new TrafiklabAPIClientException("Error when requesting StopPoint data", rce);
        } catch (JsonProcessingException jpe) {
            throw new TrafiklabAPIClientException("Failed to deserialize StopPoint response", jpe);
        }
    }

    private HttpEntity<String> createHttpEntityWithGzipHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Encoding", "gzip, deflate");
        return new HttpEntity<>(headers);
    }

}

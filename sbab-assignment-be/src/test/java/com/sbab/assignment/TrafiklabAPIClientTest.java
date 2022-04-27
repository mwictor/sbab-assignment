package com.sbab.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbab.assignment.client.TrafiklabAPIClient;
import com.sbab.assignment.domain.JourneyPatternPointOnLine;
import com.sbab.assignment.domain.StopPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrafiklabAPIClientTest {

    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private TrafiklabAPIClient client;
    private String jourUrl = "jourUrl";
    private String stopUrl = "stopUrl";

    @BeforeEach
    public void setup() {
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        client = new TrafiklabAPIClient(jourUrl, stopUrl, restTemplate, objectMapper);
    }

    @Test
    public void getJourDataDeserialization() throws IOException, ExecutionException, InterruptedException {
        //setup
        List<JourneyPatternPointOnLine> expectedList = new ArrayList<>();
        JourneyPatternPointOnLine testData = new JourneyPatternPointOnLine();
        testData.setExistsFromDate("2022-03-19 00:00:00.000");
        testData.setJourneyPatternPointNumber("1");
        testData.setDirectionCode("2");
        testData.setLineNumber("1");
        testData.setLastModifiedUtcDateTime("2022-03-19 00:00:00.000");

        JourneyPatternPointOnLine testData2 = new JourneyPatternPointOnLine();
        testData2.setExistsFromDate("2022-03-19 00:00:00.000");
        testData2.setJourneyPatternPointNumber("2");
        testData2.setDirectionCode("2");
        testData2.setLineNumber("2");
        testData2.setLastModifiedUtcDateTime("2022-03-19 00:00:00.000");

        expectedList.add(testData);
        expectedList.add(testData2);

        when(restTemplate.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<String>>any())
        ).thenReturn(createJourResponse());

        //execute
        CompletableFuture<List<JourneyPatternPointOnLine>> list = client.getJourData();
        List<JourneyPatternPointOnLine> deserializedResponse = list.get();

        //verify
        Assertions.assertTrue(expectedList.size() == deserializedResponse.size());
        Assertions.assertEquals(expectedList, deserializedResponse);
    }

    @Test
    public void getStopPointDataDeserialization() throws IOException, ExecutionException, InterruptedException {
        //setup
        List<StopPoint> expectedList = new ArrayList<>();
        StopPoint expectedData = new StopPoint();
        expectedData.setStopPointNumber("2");
        expectedData.setStopPointName("A");
        expectedData.setStopAreaNumber("1051");
        expectedData.setLocationNorthingCoordinate("59.3313179695028");
        expectedData.setLocationEastingCoordinate("18.0616773959365");
        expectedData.setZoneShortName("A");
        expectedData.setStopAreaTypeCode("BUSSTERM");
        expectedData.setLastModifiedUtcDateTime("2014-06-03 00:00:00.000");
        expectedData.setExistsFromDate("2014-06-03 00:00:00.000");
        expectedList.add(expectedData);

        when(restTemplate.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<String>>any())
        ).thenReturn(createStopResponse());

        //execute
        CompletableFuture<List<StopPoint>> list = client.getStopPointData();
        List<StopPoint> deserializedResponse = list.get();

        //verify
        assertStopPointResponse(expectedList, deserializedResponse);
    }

    private void assertStopPointResponse(List<StopPoint> expectedList, List<StopPoint> actualList) {
        Assertions.assertEquals(expectedList, actualList);
        for(int i = 0; i < expectedList.size(); i++) {
            StopPoint expectedStopPoint = expectedList.get(i);
            StopPoint actualStopPoint = actualList.get(i);

            Assertions.assertEquals(expectedStopPoint.getStopPointNumber(), actualStopPoint.getStopPointNumber());
            Assertions.assertEquals(expectedStopPoint.getStopPointName(), actualStopPoint.getStopPointName());
            Assertions.assertEquals(expectedStopPoint.getStopAreaNumber(), actualStopPoint.getStopAreaNumber());
            Assertions.assertEquals(expectedStopPoint.getLocationNorthingCoordinate(), actualStopPoint.getLocationNorthingCoordinate());
            Assertions.assertEquals(expectedStopPoint.getLocationEastingCoordinate(), actualStopPoint.getLocationEastingCoordinate());
            Assertions.assertEquals(expectedStopPoint.getZoneShortName(), actualStopPoint.getZoneShortName());
            Assertions.assertEquals(expectedStopPoint.getStopAreaTypeCode(), actualStopPoint.getStopAreaTypeCode());
            Assertions.assertEquals(expectedStopPoint.getLastModifiedUtcDateTime(), actualStopPoint.getLastModifiedUtcDateTime());
            Assertions.assertEquals(expectedStopPoint.getExistsFromDate(), actualStopPoint.getExistsFromDate());
        }
    }

    private ResponseEntity<String> createJourResponse() throws IOException {
        InputStream is = Test.class.getResourceAsStream("/data/jourResponse.json");
        String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }

    private ResponseEntity<String> createStopResponse() throws IOException {
        InputStream is = Test.class.getResourceAsStream("/data/stopResponse.json");
        String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }

}

package com.sbab.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbab.assignment.client.TrafiklabAPIClient;
import com.sbab.assignment.domain.BusLine;
import com.sbab.assignment.domain.GetTopTenBusLinesResponse;
import com.sbab.assignment.domain.JourneyPatternPointOnLine;
import com.sbab.assignment.domain.StopPoint;
import com.sbab.assignment.service.BusLineInformationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class BusLineInformationServiceTest {

    private ObjectMapper objectMapper;
    private BusLineInformationService busLineInformationService;
    private TrafiklabAPIClient trafiklabClient;

    @BeforeEach
    public void init() {
        trafiklabClient = mock(TrafiklabAPIClient.class);
        objectMapper = new ObjectMapper();
        busLineInformationService = new BusLineInformationService(trafiklabClient);
    }

    @Test
    public void sortBusLines() {
        //setup
        List<BusLine> testList = createBusLinesData();
        assertTrue("Test list is greater than 10", testList.size() > 10);

        testList = testList.subList(testList.size() -10, testList.size());
        Collections.reverse(testList);
        //execute
        List<BusLine> response = busLineInformationService.sortBusLines(testList);

        //verify
        assertEquals(testList, response);
        assertEquals(10, response.size());
    }

    @Test
    public void mapLineToStopPoints() throws IOException {
        //setup
        String[] expectedKeys = {"1", "2", "4"};
        ArrayList<String> expectedListOne = new ArrayList<>(
                Arrays.asList("A", "A")
        );
        ArrayList<String> expectedListTwo = new ArrayList<>(
                Arrays.asList("B", "C", "C", "D")
        );
        ArrayList<String> expectedListThree = new ArrayList<>(
                Arrays.asList("A", "A", "B")
        );
        Map<String, List<String>> expectedResult = new HashMap<>();
        expectedResult.put(expectedKeys[0], expectedListOne);
        expectedResult.put(expectedKeys[1], expectedListTwo);
        expectedResult.put(expectedKeys[2], expectedListThree);

        Map<String, String> mapStopPointNumberToName = new HashMap<>();
        mapStopPointNumberToName.put("1", "A");
        mapStopPointNumberToName.put("2", "A");
        mapStopPointNumberToName.put("3", "B");
        mapStopPointNumberToName.put("4", "C");
        mapStopPointNumberToName.put("5", "C");
        mapStopPointNumberToName.put("6", "D");

        //execution
        Map<String, List<String>> result = busLineInformationService.mapLineToStopPoints(createJourData("1"), mapStopPointNumberToName);

        //verify keys
        Assertions.assertArrayEquals(expectedKeys, result.keySet().toArray(new String[0]));

        //verify values
        for(int i = 0; i < expectedKeys.length; i++) {
            String key = expectedKeys[i];
            List<String> tmpExpectedList = expectedResult.get(key);
            List<String> tmpResultList = result.get(key);

            Assertions.assertEquals(expectedResult.get(key), result.get(key));
            Assertions.assertTrue(tmpExpectedList.size() == tmpResultList.size());
            Assertions.assertTrue(tmpExpectedList.containsAll(tmpResultList)
                    && tmpResultList.containsAll(tmpExpectedList));
        }
    }

    @Test
    public void mapStopPointNumberToNames() throws IOException {
        //setup
        String[] expectedKeys = {"1", "2", "3", "4", "5", "6"};

        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("1", "A");
        expectedResult.put("2", "A");
        expectedResult.put("3", "B");
        expectedResult.put("4", "C");
        expectedResult.put("5", "C");
        expectedResult.put("6", "D");

        Map<String, String> result = busLineInformationService.mapStopPointNumberToNames(createStopData("1"));

        //verify keys
        Assertions.assertArrayEquals(expectedKeys, result.keySet().toArray(new String[0]));

        //verify values
        for(int i = 0; i < expectedKeys.length; i++) {
            String key = expectedKeys[i];
            Assertions.assertEquals(expectedResult.get(key), result.get(key));
        }
    }

    @Test
    public void createAndSortBusLinesList() {
        //Expected result
        List<BusLine> expectedBusLines = new ArrayList<>();

        BusLine busLineOne = new BusLine("1", new ArrayList<>(
                Arrays.asList("A", "A")
        ));
        BusLine busLineTwo = new BusLine("2", new ArrayList<>(
               Arrays.asList("B", "C", "D", "C")
        ));
        BusLine busLineThree = new BusLine("4", new ArrayList<>(
               Arrays.asList("A", "A", "B")
        ));
        expectedBusLines.add(busLineTwo);
        expectedBusLines.add(busLineThree);
        expectedBusLines.add(busLineOne);

        //setup
        String[] expectedKeys = {"1", "2", "4"};
        ArrayList<String> expectedListOne = new ArrayList<>(
                Arrays.asList("A", "A")
        );
        ArrayList<String> expectedListTwo = new ArrayList<>(
                Arrays.asList("B", "C", "C", "D")
        );
        ArrayList<String> expectedListThree = new ArrayList<>(
                Arrays.asList("A", "A", "B")
        );
        Map<String, List<String>> testData = new HashMap<>();
        testData.put(expectedKeys[0], expectedListOne);
        testData.put(expectedKeys[1], expectedListTwo);
        testData.put(expectedKeys[2], expectedListThree);

        //execute
        List<BusLine> busLinesResult = busLineInformationService.createBusLines(testData);

        //verify
        Assertions.assertEquals(expectedBusLines.size(), busLinesResult.size());
        for(int i = 0; i < expectedBusLines.size(); i++) {
            List<String> tmpExpectedList = expectedBusLines.get(i).getListOfStopPoints();
            List<String> tmpResultList = busLinesResult.get(i).getListOfStopPoints();

            Assertions.assertEquals(expectedBusLines.get(i).getBusLine(), busLinesResult.get(i).getBusLine());
            Assertions.assertEquals(tmpExpectedList.size(), tmpResultList.size());
            Assertions.assertTrue(tmpExpectedList.containsAll(tmpResultList)
                    && tmpResultList.containsAll(tmpExpectedList));
        }
    }

    @Test
    public void pollData() throws IOException, InterruptedException {
        //setup
        List<BusLine> expectedBusLines = new ArrayList<>();

        BusLine busLineOne = new BusLine("1", new ArrayList<>(
                Arrays.asList("A", "B", "C")
        ));
        BusLine busLineTwo = new BusLine("2", new ArrayList<>(
                Arrays.asList("A")
        ));
        expectedBusLines.add(busLineOne);
        expectedBusLines.add(busLineTwo);

        when(trafiklabClient.getJourData()).thenReturn(CompletableFuture.completedFuture(createJourData("2")));
        when(trafiklabClient.getStopPointData()).thenReturn(CompletableFuture.completedFuture(createStopData("2")));

        //execute
        busLineInformationService.dataPoll();

        //verify
        List<BusLine> busLinesResult = List.copyOf(busLineInformationService.getBusLines());

        Assertions.assertTrue(expectedBusLines.size() == busLinesResult.size());
        for(int i = 0; i < expectedBusLines.size(); i++) {
            List<String> tmpExpectedList = expectedBusLines.get(i).getListOfStopPoints();
            List<String> tmpResultList = busLinesResult.get(i).getListOfStopPoints();

            Assertions.assertEquals(expectedBusLines.get(i).getBusLine(), busLinesResult.get(i).getBusLine());
            Assertions.assertEquals(tmpExpectedList.size(), tmpResultList.size());
            Assertions.assertTrue(tmpExpectedList.containsAll(tmpResultList)
                    && tmpResultList.containsAll(tmpExpectedList));
        }
    }

    private List<JourneyPatternPointOnLine> createJourData(String id) throws IOException {
        InputStream is = Test.class.getResourceAsStream("/data/journeyPatternPointOnLine_data" + id + ".json");
        return Arrays.asList(objectMapper.readValue(is, JourneyPatternPointOnLine[].class));
    }

    private List<StopPoint> createStopData(String id) throws IOException {
        InputStream is = Test.class.getResourceAsStream("/data/stopPoint_data" + id + ".json");
        return Arrays.asList(objectMapper.readValue(is, StopPoint[].class));
    }

    private List<BusLine> createBusLinesData() {
        ArrayList<BusLine> busLines = new ArrayList<>();
        for(int i = 0; i < 11; i++) {
            String line = String.valueOf(i);
            ArrayList<String> tmpList = new ArrayList<>();
            for(int j = 0; j<=i; j++) {
                String stopPoint = String.valueOf(j);
                tmpList.add(stopPoint);
            }
            busLines.add(new BusLine(line, tmpList));
        }
        return busLines;
    }

}

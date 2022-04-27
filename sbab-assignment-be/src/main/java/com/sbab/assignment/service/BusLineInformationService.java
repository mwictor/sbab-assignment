package com.sbab.assignment.service;

import com.sbab.assignment.client.TrafiklabAPIClient;
import com.sbab.assignment.exception.TrafiklabAPIClientException;
import com.sbab.assignment.domain.BusLine;
import com.sbab.assignment.domain.JourneyPatternPointOnLine;
import com.sbab.assignment.domain.StopPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BusLineInformationService {

    private TrafiklabAPIClient trafiklabClient;

    private List<BusLine> busLines = new ArrayList<>();

    public BusLineInformationService(TrafiklabAPIClient trafiklabClient) {
        this.trafiklabClient = trafiklabClient;
    }

    public List<BusLine> getBusLines() {
        return busLines;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void dataPoll() {
        try {
            Instant startTime = Instant.now();

            CompletableFuture<List<JourneyPatternPointOnLine>> jourList = trafiklabClient.getJourData();
            CompletableFuture<List<StopPoint>> stopPointList = trafiklabClient.getStopPointData();

            CompletableFuture.allOf(jourList, stopPointList).join();

            List<JourneyPatternPointOnLine> completedJourList = jourList.get();
            List<StopPoint> completedStopPointList = stopPointList.get();

            if(!completedJourList.isEmpty() && !completedStopPointList.isEmpty()) {
                Map<String, List<String>> mapOfLineToStopPoints = mapLineToStopPoints(completedJourList,
                        mapStopPointNumberToNames(completedStopPointList));
                busLines = Collections.unmodifiableList(createBusLines(mapOfLineToStopPoints));
                log.debug("Processing data took [{}] ms ", Duration.between(startTime, Instant.now()).toMillis());
            }
            log.info("Top ten buslines sorted and set");
        } catch (TrafiklabAPIClientException tace) {
            log.error("Failure when getting data from Trafiklab", tace);
        } catch (Exception e) {
            log.error("Unexpected error when getting data from Trafiklab", e);
        }
    }

    @Scheduled(cron = "${polling.rate}")
    public void pollForMissingData() {
        if(busLines.isEmpty()) {
            log.info("No data in system, will start polling for data");
            dataPoll();
        }
    }

    /*
     SL Stops and lines v2.0 api updates its data every day between 0.00-2.00. This
     method was created to fetch updated data. Cron is set to 2.01 a.m.
     */
    @Scheduled(cron = "0 1 2 * * *")
    public void pollUpdatedData() {
        log.info("Fetching updated data");
        dataPoll();
    }

    public Map<String, List<String>> mapLineToStopPoints(List<JourneyPatternPointOnLine> listOfJour,
                                                         Map<String, String> mapStopPointNumberToName) {
        Map<String, List<String>> mapOfLineToStopPoints = new HashMap<>();
        for(JourneyPatternPointOnLine jour : listOfJour) {
            String stopPoint = mapStopPointNumberToName.get(jour.getJourneyPatternPointNumber());
            if(mapOfLineToStopPoints.containsKey(jour.getLineNumber())) {
                mapOfLineToStopPoints.get(jour.getLineNumber()).add(stopPoint);
            } else {
                ArrayList<String> newList = new ArrayList<>();
                newList.add(stopPoint);
                mapOfLineToStopPoints.put(jour.getLineNumber(), newList);
            }
        }
        return mapOfLineToStopPoints;
    }

    public Map<String, String> mapStopPointNumberToNames(List<StopPoint> listOfStopPoints) {
        Map<String, String> mapOfStopPointNumberToNames = new HashMap<>();
        for(StopPoint s : listOfStopPoints) {
            mapOfStopPointNumberToNames.put(s.getStopPointNumber(), s.getStopPointName());
        }
        return mapOfStopPointNumberToNames;
    }

    public List<BusLine> createBusLines(Map<String, List<String>> mapLineToStopPoints) {
        List<BusLine> busLines = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry : mapLineToStopPoints.entrySet()) {
            BusLine busLine = new BusLine(entry.getKey(), entry.getValue());
            busLines.add(busLine);
        }
        return sortBusLines(busLines);
    }

    public List<BusLine> sortBusLines(List<BusLine> busLines) {
        busLines.sort(Comparator.comparing(o -> o.getListOfStopPoints().size()));
        if(busLines.size() > 10) {
            busLines = busLines.subList(busLines.size() -10, busLines.size());
        }
        Collections.reverse(busLines);
        return busLines;
    }

}
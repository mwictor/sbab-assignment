package com.sbab.assignment.controller;

import com.sbab.assignment.domain.BusLine;
import com.sbab.assignment.domain.GetTopTenBusLinesResponse;
import com.sbab.assignment.service.BusLineInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/lines")
@CrossOrigin(origins = "http://localhost:3000")
public class BusLineInformationController {

    private BusLineInformationService busLineInformationService;

    public BusLineInformationController(BusLineInformationService busLineInformationService) {
        this.busLineInformationService = busLineInformationService;
    }

    @GetMapping(value = "/topTen", produces = "application/json")
    public ResponseEntity<GetTopTenBusLinesResponse> getTopThenBusLines() {
        log.info("Incoming request to getTopTen endpoint");
        List<BusLine> busLines = busLineInformationService.getBusLines();
        return new ResponseEntity<>(new GetTopTenBusLinesResponse(busLines), HttpStatus.OK);
    }

}

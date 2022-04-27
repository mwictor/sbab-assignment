package com.sbab.assignment.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusLine {

    private String busLine;
    private List<String> listOfStopPoints;

    public BusLine(String busLineNumber, List<String> listOfStopPoints) {
        this.busLine = busLineNumber;
        this.listOfStopPoints = listOfStopPoints;
    }
}

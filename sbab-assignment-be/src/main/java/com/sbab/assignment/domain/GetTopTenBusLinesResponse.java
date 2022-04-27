package com.sbab.assignment.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetTopTenBusLinesResponse {

    private List<BusLine> busLines;

    public GetTopTenBusLinesResponse(List<BusLine> busLines) {
        this.busLines = busLines;
    }

}

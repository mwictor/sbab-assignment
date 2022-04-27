package com.sbab.assignment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class JourneyPatternPointOnLine {

    @JsonProperty("LineNumber")
    private String lineNumber;

    @JsonProperty("DirectionCode")
    private String directionCode;

    @JsonProperty("JourneyPatternPointNumber")
    private String journeyPatternPointNumber;

    @JsonProperty("LastModifiedUtcDateTime")
    private String lastModifiedUtcDateTime;

    @JsonProperty("ExistsFromDate")
    private String existsFromDate;

}

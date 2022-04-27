package com.sbab.assignment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StopPoint {

    @EqualsAndHashCode.Include
    @JsonProperty("StopPointNumber")
    private String stopPointNumber;

    @JsonProperty("StopPointName")
    private String stopPointName;

    @JsonProperty("StopAreaNumber")
    private String stopAreaNumber;

    @JsonProperty("LocationNorthingCoordinate")
    private String locationNorthingCoordinate;

    @JsonProperty("LocationEastingCoordinate")
    private String locationEastingCoordinate;

    @JsonProperty("ZoneShortName")
    private String zoneShortName;

    @JsonProperty("StopAreaTypeCode")
    private String stopAreaTypeCode;

    @JsonProperty("LastModifiedUtcDateTime")
    private String lastModifiedUtcDateTime;

    @JsonProperty("ExistsFromDate")
    private String existsFromDate;

}

package com.example.hatsalvoids.building.model;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildingSpecificAndSummaryResponse {
    String x;
    String y;
    String summary;
    String category;
    String buildingName;
    String buildingUrl;
    String buildingId;
    String buildingRoadAddress;
}

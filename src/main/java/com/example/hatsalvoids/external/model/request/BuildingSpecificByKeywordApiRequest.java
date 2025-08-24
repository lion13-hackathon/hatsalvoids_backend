package com.example.hatsalvoids.external.model.request;


import lombok.Getter;

import java.util.Map;

@Getter
public class BuildingSpecificByKeywordApiRequest {
    private String x;
    private String y;
    private String radius;
    private String keyword;

    private BuildingSpecificByKeywordApiRequest(String x, String y, String radius, String keyword) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.keyword = keyword;
    }

    public static BuildingSpecificByKeywordApiRequest of(String x, String y, String radius, String keyword) {
        return new BuildingSpecificByKeywordApiRequest(x, y, radius, keyword);
    }

    public Map<String,String> toQueryParams() {
        return Map.of(
                "x", x,
                "y", y,
                "radius", radius,
                "query", keyword
        );
    }
}

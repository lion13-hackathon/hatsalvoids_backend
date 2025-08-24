package com.example.hatsalvoids.shade.model;


import com.example.hatsalvoids.external.vworld.model.response.RoadAddressBuildingApiResponse;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FetchShadeResponse {
    RoadAddressBuildingApiResponse.PropertiesDto properties;
    ShadeGeometryResult shadeGeometryResults;

    private FetchShadeResponse(RoadAddressBuildingApiResponse.PropertiesDto properties, ShadeGeometryResult shadeGeometryResults) {
        this.properties = properties;
        this.shadeGeometryResults = shadeGeometryResults;
    }

    public static FetchShadeResponse of(RoadAddressBuildingApiResponse.PropertiesDto properties, ShadeGeometryResult shadeGeometryResults) {
        return new FetchShadeResponse(properties, shadeGeometryResults);
    }
}

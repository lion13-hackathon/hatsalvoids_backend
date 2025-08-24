package com.example.hatsalvoids.external.vworld.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoadAddressBuildingApiResponse {

    private ServiceDto service;
    private String status;
    private RecordDto record;
    private PageDto page;
    private ResultDto result;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class ServiceDto {
        private String name;
        private String version;
        private String operation;
        private String time;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class RecordDto {
        private String total;
        private String current;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class PageDto {
        private String total;
        private String current;
        private String size;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class ResultDto {
        private FeatureCollectionDto featureCollection;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class FeatureCollectionDto {
        private String type;
        private List<Double> bbox;
        private List<FeatureDto> features;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class FeatureDto {
        private String type;
        private GeometryDto geometry;
        private PropertiesDto properties;
        private String id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class GeometryDto {
        private String type;
        // GeoJSON coordinates: [ [ [ [x, y], ... ] ] ]
        private List<List<List<List<Double>>>> coordinates;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class PropertiesDto {
        private String bd_mgt_sn;
        private String buld_nm;
        private String bul_eng_nm;
        private String gro_flo_co;
        private String sido;
        private String sigungu;
        private String gu;
        private String rd_nm;
        private String buld_no;
    }
}



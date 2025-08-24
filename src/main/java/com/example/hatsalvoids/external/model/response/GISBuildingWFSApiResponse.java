package com.example.hatsalvoids.external.model.response;

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
public class GISBuildingWFSApiResponse {

    private String type; // FeatureCollection
    private List<Feature> features;
    private Integer totalFeatures;
    private Integer numberMatched;
    private Integer numberReturned;
    private String timeStamp;
    private Crs crs;
    private List<Double> bbox;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Feature {
        private String type; // Feature
        private String id;
        private Geometry geometry;
        private String geometry_name;
        private Properties properties;
        private List<Double> bbox;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Geometry {
        private String type; // MultiPolygon
        // coordinates: [ [ [ [x, y], ... ] ] ]
        private List<List<List<List<Double>>>> coordinates;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Properties {
        private Integer src_objectid;
        private String gis_idntfc_no;
        private String pnu;
        private String ld_cpsg_code;
        private String mnnm;
        private String slno;
        private String regstr_se_code;
        private String buld_prpos_code;
        private String strct_code;
        private Double ar;
        private String use_confm_de;
        private Double totar;
        private Double plot_ar;
        private Double hg; // 건물 높이(m)
        private Double btl_rt;
        private Double measrmt_rt;
        private String buld_idntfc_no;
        private String violt_bild;
        private String refrn_systm_cntc_no;
        private String last_updt_dt;
        private String src_signgu_code;
        private String buld_nm;
        private String dong_nm;
        private Integer ground_floor_co;
        private Integer undgrnd_floor_co;
        private String data_creat_change_de;


        public Double getHg(){
            double DEFAULT_HEIGHT = 8.0; // 기본 건물 높이 (미터 단위)

            if(hg == 0.0){
                return DEFAULT_HEIGHT;
            }

            return hg;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Crs {
        private String type; // name
        private CrsProperties properties;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrsProperties {
        private String name; // urn:ogc:def:crs:EPSG::5186
    }
}



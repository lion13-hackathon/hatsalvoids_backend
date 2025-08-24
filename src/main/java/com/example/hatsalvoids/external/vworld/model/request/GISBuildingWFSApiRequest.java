package com.example.hatsalvoids.external.vworld.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GISBuildingWFSApiRequest {

    // 필수
    @NotBlank
    private String typename; // 예: dt_d010

    @NotNull
    private Double minLat;

    @NotNull
    private Double minLon;

    @NotNull
    private Double maxLat;

    @NotNull
    private Double maxLon;

    @NotBlank
    private String bboxCrs; // 예: EPSG:4326

    @NotBlank
    private String srsName; // 예: EPSG:5186 (응답 좌표계)

    @NotBlank
    private String output; // 예: application/json

    @NotBlank
    private String key; // API Key

    @NotBlank
    private String domain; // 예: www.hatsalvoids.com

    // 선택
    private String pnu; // 지번

    @Min(1)
    @Max(1000)
    private Integer maxFeatures; // 예: 10

    private String resultType; // 예: results

    /**
     * VWorld 건축물 WFS API 호출용 요청 객체 생성 (고정 파라미터 내장)
     * @param key    API Key (필수)
     * @param domain 도메인 (필수)
     * @param pnu    지번 (선택)
     * @param maxFeatures 최대 결과 수 (선택)
     * @return GISBuildingWFSRequest
     */
    public static GISBuildingWFSApiRequest of(
            String key,
            String domain,
            String pnu,
            Integer maxFeatures
    ) {
        return GISBuildingWFSApiRequest.builder()
                .typename("dt_d010")
                .pnu(pnu)
                .maxFeatures(maxFeatures)
                .resultType("results")
                .srsName("EPSG:5186")
                .output("application/json")
                .key(key)
                .domain(domain)
                .build();
    }
    public Map<String, String> toQueryParams() {
        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("typename", typename);
        if (pnu != null && !pnu.isBlank()) {
            queryParams.put("pnu", pnu);
        }
        if (maxFeatures != null) {
            queryParams.put("maxFeatures", String.valueOf(maxFeatures));
        }
        if (resultType != null && !resultType.isBlank()) {
            queryParams.put("resultType", resultType);
        }
        queryParams.put("srsName", srsName);
        queryParams.put("output", output);
        queryParams.put("key", key);
        queryParams.put("domain", domain);
        return queryParams;
    }

}



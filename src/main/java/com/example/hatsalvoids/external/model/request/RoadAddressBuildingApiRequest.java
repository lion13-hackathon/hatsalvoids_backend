package com.example.hatsalvoids.external.model.request;

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

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoadAddressBuildingApiRequest {

    @NotBlank
    private String service; // e.g., "data"

    @NotBlank
    private String version; // e.g., "2.0"

    @NotBlank
    private String request; // e.g., "GetFeature"

    @NotBlank
    private String format; // e.g., "json"

    @NotBlank
    private String errorformat; // e.g., "xml"

    @NotNull
    @Min(1)
    @Max(1000)
    private Integer size; // e.g., 10

    @NotNull
    @Min(1)
    private Integer page; // e.g., 1

    @NotBlank
    private String data; // e.g., "LT_C_SPBD"

    private String attrFilter; // e.g., "bd_mgt_sn:like:..."

    private String geomFilter;

    private String columns; // e.g., "bd_mgt_sn,buld_nm,...,ag_geom"

    // "true" / "false" 로 전달되는 API 스펙을 맞추기 위해 문자열로 유지
    private String geometry; // e.g., "true"

    private String attribute; // e.g., "true"

    private String crs; // e.g., "EPSG:5186"

    private String domain; // e.g., "www.hatsalvoids.com"

    @NotBlank
    private String key; // API key


    public static RoadAddressBuildingApiRequest of
            (int page, int size, String geometryFilter, String apiKey, String domain) {
        return RoadAddressBuildingApiRequest.builder()
                .service("data")
                .version("2.0")
                .request("GetFeature")
                .format("json")
                .errorformat("json")
                .columns("bd_mgt_sn,gro_flo_co,buld_nm,gro_flo_co,sido,sigungu,gu,rd_nm,buld_no,ag_geom")
                .data("LT_C_SPBD")
                .geometry("true")
                .attribute("true")
                .crs("EPSG:5186")
                .geomFilter(geometryFilter) // TODO: 동적 필터링 필요시 파라미터화
                .size(size)
                .page(page)
                .key(apiKey)
                .domain(domain)
                .build();
    }

    public Map<String, String> toQueryParams(){
        Map<String, String> queryParams = new HashMap<>();

        queryParams.put("service", service);
        queryParams.put("version", version);
        queryParams.put("geometry", geometry);
        queryParams.put("request", request);
        queryParams.put("columns", "bd_mgt_sn,gro_flo_co,buld_nm,gro_flo_co,sido,sigungu,gu,rd_nm,buld_no,ag_geom");
        queryParams.put("attribute", attribute);
        queryParams.put("crs", crs);
        queryParams.put("format", format);
        queryParams.put("errorformat", errorformat);
        queryParams.put("data", data);
        queryParams.put("geomFilter", geomFilter); // TODO: 동적 필터링 필요시 파라미터화
        queryParams.put("size", String.valueOf(size));
        queryParams.put("page", String.valueOf(page));
        queryParams.put("key", key);
        queryParams.put("domain", domain);
        // queryParams.put("attrfilter", "emdCd:=:30200111|rd_nm:like:문화원"); // GeoFilter로 해결 가능하다고 생각해서 일단 주석처리

        return queryParams;
    }
}



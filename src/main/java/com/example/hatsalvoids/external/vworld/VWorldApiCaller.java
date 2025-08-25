package com.example.hatsalvoids.external.vworld;


import com.example.hatsalvoids.external.ExternalApiCaller;
import com.example.hatsalvoids.external.vworld.model.request.GISBuildingWFSApiRequest;
import com.example.hatsalvoids.external.vworld.model.request.RoadAddressBuildingApiRequest;
import com.example.hatsalvoids.external.vworld.model.response.GISBuildingWFSApiResponse;
import com.example.hatsalvoids.external.vworld.model.response.RoadAddressBuildingApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VWorldApiCaller {

    private final ExternalApiCaller externalApiCaller;

    @Value("${vworld.api-key}")
    private String apiKey;

    @Value("${vworld.domain}")
    private String domain;


    public List<RoadAddressBuildingApiResponse> getRoadAddressBuildingAroundAll
            (String geometryFilter) {

        int page = 1;
        int totalPage = Integer.MAX_VALUE;
        int size = 100; // 페이지당 최대 1000개 데이터 조회 가능

        ArrayList<RoadAddressBuildingApiResponse> results = new ArrayList<>();

        while(page < totalPage){
            RoadAddressBuildingApiResponse response = getRoadAddressBuildingAround(page, size, geometryFilter);

            page = Integer.parseInt(response.getPage().getCurrent());
            totalPage = Integer.parseInt(response.getPage().getTotal());

            results.add(response);
        }

        return results;
    }
    public RoadAddressBuildingApiResponse getRoadAddressBuildingAround
            (int page, int size, String geometryFilter) {
        String url = "https://api.vworld.kr/req/data";

        RoadAddressBuildingApiRequest request =
                RoadAddressBuildingApiRequest.of(page, size, geometryFilter, apiKey, domain);

        return externalApiCaller.getVworldRegacy(
                url,
                null,
                request.toQueryParams(),
                new ParameterizedTypeReference<RoadAddressBuildingApiResponse>() {
                }
        );
    }

    public GISBuildingWFSApiResponse getGISBuildingWFS(String pnu) {
        String url = "https://api.vworld.kr/ned/wfs/getBldgisSpceWFS";

        GISBuildingWFSApiRequest request =
                GISBuildingWFSApiRequest.of(apiKey, domain, pnu, 1);

        return externalApiCaller.getVworldRegacy(
                url,
                null,
                request.toQueryParams(),
                new ParameterizedTypeReference<GISBuildingWFSApiResponse>() {
                }
        );
    }
}

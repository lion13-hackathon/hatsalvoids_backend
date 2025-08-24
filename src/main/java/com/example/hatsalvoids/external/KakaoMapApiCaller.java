package com.example.hatsalvoids.external;


import com.example.hatsalvoids.external.model.request.BuildingSpecificByKeywordApiRequest;
import com.example.hatsalvoids.external.model.response.BuildingSpecificsByKeywordApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoMapApiCaller {

    private final ExternalApiCaller externalApiCaller;

    @Value("${kakao.api-key}")
    private String apiKey;

    public BuildingSpecificsByKeywordApiResponse getBuildingSpecificsByKeyword(String x, String y, String radius, String keyword) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json";

        BuildingSpecificByKeywordApiRequest request =
                BuildingSpecificByKeywordApiRequest.of(x, y, radius, keyword);

        return externalApiCaller.get(
                url,
                new HashMap<>(Map.of("Authorization", "KakaoAK " + apiKey)),
                request.toQueryParams(),
                new ParameterizedTypeReference<BuildingSpecificsByKeywordApiResponse>() {
                }
        );
    }
}
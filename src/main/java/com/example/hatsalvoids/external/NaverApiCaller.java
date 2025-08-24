package com.example.hatsalvoids.external;

import com.example.hatsalvoids.external.model.request.NaverBlogSearchRequest;
import com.example.hatsalvoids.external.model.response.NaverBlogSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class NaverApiCaller {
    private final ExternalApiCaller externalApiCaller;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;



    public NaverBlogSearchResponse getBlogSearchResult(String query){
        NaverBlogSearchRequest request = NaverBlogSearchRequest.of(query, "10");

        return externalApiCaller.get(
                "https://openapi.naver.com/v1/search/blog.json",
                getHeader(),
                request.toQueryParams(),
                new ParameterizedTypeReference<NaverBlogSearchResponse>() {}
        );
    }

    private Map<String, String> getHeader(){
        return Map.of(
                "X-Naver-Client-Id", clientId,
                "X-Naver-Client-Secret", clientSecret
        );
    }
}

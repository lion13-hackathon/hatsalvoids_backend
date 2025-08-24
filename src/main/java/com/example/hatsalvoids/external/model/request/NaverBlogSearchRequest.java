package com.example.hatsalvoids.external.model.request;


import lombok.Getter;

import java.util.Map;

@Getter
public class NaverBlogSearchRequest {
    String query;
    String display;

    private NaverBlogSearchRequest(String query, String display) {
        this.query = query;
        this.display = display;
    }

    public static NaverBlogSearchRequest of(String query, String display) {
        return new NaverBlogSearchRequest(query, display);
    }

    public Map<String,String> toQueryParams() {
        return Map.of(
                "query", query,
                "display", display
        );
    }
}

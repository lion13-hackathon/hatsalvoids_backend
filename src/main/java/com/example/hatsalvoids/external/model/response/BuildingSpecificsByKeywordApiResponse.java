package com.example.hatsalvoids.external.model.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BuildingSpecificsByKeywordApiResponse {
    List<Documents> documents;

    @Getter
    @ToString
    static public class Documents {

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("category_group_code")
        private String categoryGroupCode;

        @JsonProperty("category_group_name")
        private String categoryGroupName;

        @JsonProperty("category_name")
        private String categoryName;

        private String distance;
        private String id;
        private String phone;

        @JsonProperty("place_name")
        private String placeName;

        @JsonProperty("place_url")
        private String placeUrl;

        @JsonProperty("road_address_name")
        private String roadAddressName;
        private String x;
        private String y;
    }
}

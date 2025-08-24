package com.example.hatsalvoids.building;


import com.example.hatsalvoids.external.NaverApiCaller;
import com.example.hatsalvoids.external.model.response.BuildingSpecificsByKeywordApiResponse;
import com.example.hatsalvoids.external.KakaoMapApiCaller;
import com.example.hatsalvoids.external.OpenAiService;
import com.example.hatsalvoids.external.model.response.NaverBlogSearchResponse;
import com.example.hatsalvoids.global.utils.GlobalLogger;
import com.example.hatsalvoids.global.utils.NaverBlogCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final KakaoMapApiCaller kakaoMapApiCaller;
    private final NaverApiCaller naverApiCaller;
    private final NaverBlogCrawler naverBlogCrawler;
    private final OpenAiService openAiService;

    // TODO : 웹소켓으로 연결해서 GPT 요약 요청을 비동기로 처리하여 처리하는 족족 출력시키도록 수행
    public List<BuildingSpecificsByKeywordApiResponse.Documents> getBuildingSpecificsByKeyword(String x, String y, String radius, String keyword) {

        BuildingSpecificsByKeywordApiResponse apiResponse =
                kakaoMapApiCaller.getBuildingSpecificsByKeyword(x, y, radius, keyword);

        return apiResponse.getDocuments();
    }

    @Async
    public String getBuildingSummaryAsync(BuildingSpecificsByKeywordApiResponse.Documents buildingSpecific) throws ExecutionException, InterruptedException {
        GlobalLogger.info("getBuildingSummaryAsync:",buildingSpecific);
        NaverBlogSearchResponse blogSearchResult =
                naverApiCaller.getBlogSearchResult(buildingSpecific.getPlaceName());

        StringBuilder contextBuilder = new StringBuilder();

        // 네이버 블로그 글을 파싱하여 컨텍스트 빌더에 추가
        blogSearchResult.getItems()
                .forEach(item -> contextBuilder.append(naverBlogCrawler.blogCrawling(item.getLink())));

        String context = contextBuilder.toString().trim();
        GlobalLogger.info("getBuildingSummaryAsync contextBuilder:", context);

        String template = """
                # 프롬프트
                                
                다음 입력을 바탕으로 **네이버 블로그 게시글 및 카테고리와 어울리는** 광고 문구 1개를 작성하세요.
                
                반드시 아래 제약을 지키십시오.
                                
                ## 입력
                                
                * 장소명: %s
                * 카테고리: %s
                * 블로그 요약: %s
                                
                ## 작성 규칙 및 산출물 형식
                                
                * 출력은 **한 줄**만.
                * **카테고리,블로그 요약 및 어울리는 핵심 가치** 중심으로 작성.
                * 홍보하는 듯이 30자 이내로 작성, 다채로운 표현 사용
                * ~요 말투를 사용
                * **해시태그, 이모지, 따옴표, URL 금지**.
                * 과장/허위 금지. **입력에 없으면 쓰지 말 것**.
                * 부정적인 표현은 삼가
                
                * **블로그 요약 또는 키워드에 없는 정보 금지**.
                                
                **위 조건을 모두 통과한 최종 문구 한 줄만 출력하세요.**
                """.formatted(
                buildingSpecific.getPlaceName(),
                buildingSpecific.getCategoryName(),
                context);


        return openAiService.getGeneratedTextAsync(template).get();
    }

}

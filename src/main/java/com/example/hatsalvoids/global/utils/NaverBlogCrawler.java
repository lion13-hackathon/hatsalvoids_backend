package com.example.hatsalvoids.global.utils;


import com.example.hatsalvoids.external.ExternalApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@Component
@RequiredArgsConstructor
public class NaverBlogCrawler {

    private final ExternalApiCaller externalApiCaller;

    public String blogCrawling(String url){
        String mobileUrl = url.replace("://blog", "://m.blog");
        String html = externalApiCaller.get(mobileUrl);

        // HTML 파싱
        GlobalLogger.info("blogCrawling Crawling URL: " + mobileUrl);
        Document doc = Jsoup.parse(html);

        // id가 viewTypeSelector인 div 선택
        Element targetDiv = doc.getElementById("viewTypeSelector");

        StringBuilder result = new StringBuilder();

        if (targetDiv != null) {
            // <p> 태그의 텍스트를 결과에 추가
           targetDiv.select("p").forEach(p -> {
                result.append(p.text().trim()).append(" ");
            });

            // span 태그의 텍스트를 결과에 추가
            targetDiv.select("span").forEach(span -> {
                result.append(span.text().trim()).append(" ");
            });
        }

        return result.toString();
    }
}

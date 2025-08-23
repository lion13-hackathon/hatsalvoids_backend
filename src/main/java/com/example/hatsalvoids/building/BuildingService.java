package com.example.hatsalvoids.building;


import com.example.hatsalvoids.external.KakaoMapApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final KakaoMapApiCaller kakaoMapApiCaller;

    // TODO : 웹소켓으로 연결해서 GPT 요약 요청을 비동기로 처리하여 처리하는 족족 출력시키도록 수행
    public void getBuildingSpecificsAndOneLineSummaryByKeyword(String x, String y, String radius, String keyword) {
        return ;
    }


}

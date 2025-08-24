package com.example.hatsalvoids.global.websocket;

import com.example.hatsalvoids.building.BuildingService;
import com.example.hatsalvoids.building.model.BuildingSpecificAndSummaryRequest;
import com.example.hatsalvoids.building.model.BuildingSpecificAndSummaryResponse;
import com.example.hatsalvoids.external.kakao.model.response.BuildingSpecificsByKeywordApiResponse;
import com.example.hatsalvoids.global.utils.GlobalLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class EchoWebSocketHandler extends TextWebSocketHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final BuildingService buildingService;

    public EchoWebSocketHandler(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        GlobalLogger.info("Connected: ", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage raw) throws Exception {
        String payload = raw.getPayload();

        WebSocketRequestMessage req = getWebSocketResponseMessage(session, payload);

        // 라우팅: type 에 따라 분기
        switch (req.getType()) {
            case "buildingSpec" -> handleBuildingSpecificAndSummary(session, req);
            default -> sendError(session,
                    WebSocketErrorMessage.of("UNSUPPORTED_TYPE", "Unsupported type: " + req.getType(), null));
        }
    }

    private WebSocketRequestMessage getWebSocketResponseMessage(WebSocketSession session, String payload) throws Exception {
        WebSocketRequestMessage req;

        GlobalLogger.info("Received message: ", payload);

        try {
            req = objectMapper.readValue(payload, WebSocketRequestMessage.class);
            GlobalLogger.info("Parsed request: ", req);

            if(req == null) throw new Exception("값이 비어있습니다.");

        } catch (JsonProcessingException e) {
            GlobalLogger.error("JSON parsing error: ", e.getMessage());
            sendError(session,
                    WebSocketErrorMessage.of("BAD_REQUEST", "Invalid JSON format", Map.of("raw", truncate(payload, 500))));
            return null;
        }
        return req;
    }


    private void handleBuildingSpecificAndSummary(WebSocketSession session, WebSocketRequestMessage req) throws Exception {
        BuildingSpecificAndSummaryRequest requestPayload =
                objectMapper.convertValue(req.getPayload(), BuildingSpecificAndSummaryRequest.class);

        String x = requestPayload.getX();
        String y = requestPayload.getY();
        String radius = requestPayload.getRadius();

        String[] categories = {"카페","음식점","약국"};

        for(String category : categories) {
            List<BuildingSpecificsByKeywordApiResponse.Documents> buildingSpecifics =
                    buildingService.getBuildingSpecificsByKeyword(x, y, radius, category);

            // TODO : 건물마다 요약 메시지 및 건물 응답을 비동기 전송
            for(BuildingSpecificsByKeywordApiResponse.Documents buildingSpecific : buildingSpecifics){
                sendBuildingSpecificMessagePipelineAsync(session, req, buildingSpecific);
            }
        }

        GlobalLogger.info("전송 완료");
    }

    @Async("generalTaskExecutor")
    public void sendBuildingSpecificMessagePipelineAsync(WebSocketSession session,
                                                         WebSocketRequestMessage req,
                                                         BuildingSpecificsByKeywordApiResponse.Documents buildingSpecific) throws IOException, ExecutionException, InterruptedException {

        String summary = buildingService.getBuildingSummaryAsync(buildingSpecific);

        BuildingSpecificAndSummaryResponse data = BuildingSpecificAndSummaryResponse.builder()
                .x(buildingSpecific.getX())
                .y(buildingSpecific.getY())
                .category(buildingSpecific.getCategoryGroupName())
                .buildingId(buildingSpecific.getId())
                .buildingUrl(buildingSpecific.getPlaceUrl())
                .buildingName(buildingSpecific.getPlaceName())
                .buildingRoadAddress(buildingSpecific.getRoadAddressName())
                .summary(summary)
                .build();

        WebSocketResponseMessage response =
                WebSocketResponseMessage.of("message", req.getRequestId(), data);

        session.sendMessage(toTextMessage(response));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        GlobalLogger.info("Closed: ", session.getId(), " / ", status);
    }
    private TextMessage toTextMessage(Object obj) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(obj);
        return new TextMessage(json.getBytes(StandardCharsets.UTF_8));
    }

    private void sendError(WebSocketSession session, WebSocketErrorMessage error) throws Exception {
        WebSocketRequestMessage res = WebSocketRequestMessage.of("error", null, error);
        session.sendMessage(toTextMessage(res));
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) + "...(truncated)" : s;
    }
}

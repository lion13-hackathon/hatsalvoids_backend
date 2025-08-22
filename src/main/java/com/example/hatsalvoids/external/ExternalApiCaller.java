package com.example.hatsalvoids.external;

import com.example.hatsalvoids.external.exception.ExternalApiErrorCode;
import com.example.hatsalvoids.external.exception.ExternalApiException;
import com.example.hatsalvoids.global.utils.GlobalLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class ExternalApiCaller {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ExternalApiCaller() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private <T> T execute(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (e instanceof ExternalApiException) {
                throw (ExternalApiException) e;
            }
            GlobalLogger.error(ExternalApiErrorCode.REQUEST_FAIL, ":", e.getMessage());
            throw new ExternalApiException(ExternalApiErrorCode.REQUEST_FAIL, e);
        }
    }

    public <R> R get(String url, Map<String, String> headers, Map<String, String> queryParams, ParameterizedTypeReference<R> responseType) {
        return execute(() -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            if (queryParams != null) {
                queryParams.forEach((key, value) -> {
                    String encodedValue;
                    try {
                        encodedValue = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    builder.queryParam(key, encodedValue);
                });
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }

            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            URI finalUri = builder.build(true).toUri();

            GlobalLogger.info("ExternalApiCaller", "GET Request to URL: " + finalUri);
            GlobalLogger.info("Headers: " + headers);
            GlobalLogger.info("Query Params: " + queryParams);  // raw 로그

            String body;
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        finalUri,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<String>() {}
                );
                body = response.getBody();
            } catch (HttpStatusCodeException httpEx) {
                body = httpEx.getResponseBodyAsString();
            }
            GlobalLogger.info("Response: " + body);

            if (body == null || body.isEmpty()) {
                return null;
            }

            try {
                JsonNode root = objectMapper.readTree(body);
                // VWorld Data API는 { "response": { ... } } 래핑을 사용
                if (root.has("response") && root.get("response").isObject()) {
                    JsonNode resp = root.get("response");
                    String status = resp.path("status").asText("");
                    if ("ERROR".equalsIgnoreCase(status)) {
                        String code = resp.path("error").path("code").asText("");
                        String text = resp.path("error").path("text").asText("");
                        GlobalLogger.error(ExternalApiErrorCode.RESPONSE_ERROR, ": code=", code, ", text=", text);
                        throw new ExternalApiException(ExternalApiErrorCode.RESPONSE_ERROR, code, text);
                    }
                    // 정상인 경우 내부 response 노드를 요청 타입으로 매핑
                    TypeFactory tf = objectMapper.getTypeFactory();
                    return objectMapper.readValue(
                            objectMapper.treeAsTokens(resp),
                            tf.constructType(responseType.getType())
                    );
                }
                // 래핑이 없다면 전체 바디를 요청 타입으로 매핑 (예: GIS WFS 등)
                TypeFactory tf = objectMapper.getTypeFactory();
                return objectMapper.readValue(body, tf.constructType(responseType.getType()));
            } catch (ExternalApiException e) {
                throw e;
            } catch (Exception e) {
                GlobalLogger.error(ExternalApiErrorCode.REQUEST_FAIL, ":", e.getMessage());
                throw new ExternalApiException(ExternalApiErrorCode.REQUEST_FAIL, e);
            }
        });
    }


    public <T, R> R post(String url, Map<String, String> headers, Map<String, String> queryParams, T body, Class<R> responseType) {
        return execute(() -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            if (queryParams != null) {
                queryParams.forEach(builder::queryParam);
            }
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<T> entity = new HttpEntity<>(body, httpHeaders);
            String responseBody;
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<String>() {}
                );
                responseBody = response.getBody();
            } catch (HttpStatusCodeException httpEx) {
                responseBody = httpEx.getResponseBodyAsString();
            }
            GlobalLogger.info("Response: " + responseBody);

            if (responseBody == null || responseBody.isEmpty()) {
                return null;
            }
            try {
                JsonNode root = objectMapper.readTree(responseBody);
                if (root.has("response") && root.get("response").isObject()) {
                    JsonNode resp = root.get("response");
                    String status = resp.path("status").asText("");
                    if ("ERROR".equalsIgnoreCase(status)) {
                        String code = resp.path("error").path("code").asText("");
                        String text = resp.path("error").path("text").asText("");
                        GlobalLogger.error(ExternalApiErrorCode.RESPONSE_ERROR, ": code=", code, ", text=", text);
                        throw new ExternalApiException(ExternalApiErrorCode.RESPONSE_ERROR, code, text);
                    }
                    return objectMapper.readValue(
                            objectMapper.treeAsTokens(resp),
                            responseType
                    );
                }
                return objectMapper.readValue(responseBody, responseType);
            } catch (ExternalApiException e) {
                throw e;
            } catch (Exception e) {
                GlobalLogger.error(ExternalApiErrorCode.REQUEST_FAIL, ":", e.getMessage());
                throw new ExternalApiException(ExternalApiErrorCode.REQUEST_FAIL, e);
            }
        });
    }
} 
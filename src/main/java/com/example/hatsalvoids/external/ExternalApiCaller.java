package com.example.hatsalvoids.external;

import com.example.hatsalvoids.external.exception.ExternalApiErrorCode;
import com.example.hatsalvoids.external.exception.ExternalApiException;
import com.example.hatsalvoids.global.utils.GlobalLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class ExternalApiCaller {
    private final RestTemplate restTemplate;

    public ExternalApiCaller() {
        this.restTemplate = new RestTemplate();
    }

    public <T> T execute(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            GlobalLogger.error(ExternalApiErrorCode.REQUEST_FAIL,":",e.getMessage());
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

            ResponseEntity<R> response = restTemplate.exchange(
                    finalUri,
                    HttpMethod.GET,
                    entity,
                    responseType
            );

            GlobalLogger.info("Response: " + response.getBody());

            return response.getBody();
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
            ResponseEntity<R> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            return response.getBody();
        });
    }
} 
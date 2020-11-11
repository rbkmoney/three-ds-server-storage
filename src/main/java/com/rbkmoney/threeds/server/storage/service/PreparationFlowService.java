package com.rbkmoney.threeds.server.storage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.exception.MessageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreparationFlowService {

    @Value("${client.three-ds-server.url}")
    private String url;
    @Value("${client.three-ds-server.timeout}")
    private int networkTimeout;
    private final PreparationFlowDataUpdater dataUpdater;
    private final SerialNumService serialNumService;
    private final ObjectMapper objectMapper;

    public void init(String providerId, String messageVersion) {
        try {
            String serialNum = serialNumService.get(providerId).orElse(null);

            RBKMoneyPreparationRequest rbkMoneyPreparationRequest = RBKMoneyPreparationRequest.builder()
                    .providerId(providerId)
                    .serialNum(serialNum)
                    .build();
            rbkMoneyPreparationRequest.setMessageVersion(messageVersion);

            log.info("ASYNC request to 'three-ds-server' service: request={}", rbkMoneyPreparationRequest.toString());

            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(networkTimeout))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(rbkMoneyPreparationRequest)))
                    .build();

            HttpClient.newBuilder().build()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(
                            content -> {
                                try {
                                    RBKMoneyPreparationResponse rbkMoneyPreparationResponse = objectMapper.readValue(content, RBKMoneyPreparationResponse.class);

                                    log.info("ASYNC response from 'three-ds-serve'r service: response={}", rbkMoneyPreparationResponse.toString());

                                    dataUpdater.update(rbkMoneyPreparationResponse);
                                } catch (JsonProcessingException e) {
                                    throw new MessageTypeException(content, e);
                                }
                            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

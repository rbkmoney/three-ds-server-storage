package com.rbkmoney.threeds.server.storage.client;

import com.rbkmoney.threeds.server.domain.root.Message;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThreeDsClient {

    @Value("${client.three-ds-server.url}")
    private String url;

    private final RestTemplate restTemplate;

    public Message preparationFlow(String providerId, String messageVersion, String serialNumber) {
        RBKMoneyPreparationRequest rbkMoneyPreparationRequest = rbkMoneyPreparationRequest(providerId, messageVersion, serialNumber);

        log.info("Request to 'three-ds-server' service: request={}", rbkMoneyPreparationRequest.toString());

        Message message = restTemplate.postForObject(url, rbkMoneyPreparationRequest, Message.class);

        log.info("Response from 'three-ds-server' service: response={}", message.toString());

        return message;
    }

    private RBKMoneyPreparationRequest rbkMoneyPreparationRequest(String providerId, String messageVersion, String serialNumber) {
        RBKMoneyPreparationRequest rbkMoneyPreparationRequest = RBKMoneyPreparationRequest.builder()
                .providerId(providerId)
                .serialNumber(serialNumber)
                .build();
        rbkMoneyPreparationRequest.setMessageVersion(messageVersion);
        return rbkMoneyPreparationRequest;
    }
}

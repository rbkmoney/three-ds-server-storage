package com.rbkmoney.threeds.server.storage.client;

import com.rbkmoney.threeds.server.domain.root.Message;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.exception.MessageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThreeDsClient {

    @Value("${client.three-ds-server.url}")
    private String url;

    private final RestTemplate restTemplate;

    public RBKMoneyPreparationResponse preparationFlow(String providerId, String messageVersion, String serialNum) {
        RBKMoneyPreparationRequest rbkMoneyPreparationRequest = rbkMoneyPreparationRequest(providerId, messageVersion, serialNum);

        log.info("Request to 'three-ds-server' service: request={}", rbkMoneyPreparationRequest.toString());

        Message message = restTemplate.postForObject(url, rbkMoneyPreparationRequest, Message.class);

        if (message instanceof RBKMoneyPreparationResponse) {
            RBKMoneyPreparationResponse rbkMoneyPreparationResponse = (RBKMoneyPreparationResponse) message;

            log.info("Response from 'three-ds-serve'r service: response={}", rbkMoneyPreparationResponse.toString());

            return rbkMoneyPreparationResponse;
        } else {
            throw new MessageTypeException(
                    Optional.ofNullable(message).map(Message::toString).orElse("Empty body"));
        }
    }

    private RBKMoneyPreparationRequest rbkMoneyPreparationRequest(String providerId, String messageVersion, String serialNum) {
        RBKMoneyPreparationRequest rbkMoneyPreparationRequest = RBKMoneyPreparationRequest.builder()
                .providerId(providerId)
                .serialNum(serialNum)
                .build();
        rbkMoneyPreparationRequest.setMessageVersion(messageVersion);
        return rbkMoneyPreparationRequest;
    }
}

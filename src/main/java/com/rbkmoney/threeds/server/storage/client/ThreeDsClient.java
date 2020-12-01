package com.rbkmoney.threeds.server.storage.client;

import com.rbkmoney.threeds.server.domain.root.Message;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
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

    private static final String REQUEST_LOG = "-> Req [{}]: providerId={}, request={}";
    private static final String RESPONSE_LOG = "<- Res [{}]: providerId={}, response={}";

    @Value("${client.three-ds-server.url}")
    private String url;

    private final RestTemplate restTemplate;

    public Optional<Message> preparationFlow(String providerId, String messageVersion, String serialNumber) {
        String endpoint = "POST " + url;

        var request = rbkMoneyPreparationRequest(providerId, messageVersion, serialNumber);

        log.info(REQUEST_LOG, endpoint, providerId, request.toString());

        var response = Optional.ofNullable(restTemplate.postForObject(url, request, Message.class));

        log.info(RESPONSE_LOG, endpoint, providerId, response.toString());

        return response;
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

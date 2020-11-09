package com.rbkmoney.threeds.server.storage.client;

import com.rbkmoney.threeds.server.domain.root.Message;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.exception.MessageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreeDsServerClient {

    private final RestTemplate restTemplate;

    @Value("${client.three-ds-server.url}")
    private String url;

    public RBKMoneyPreparationResponse preparationFlow(RBKMoneyPreparationRequest request) {
        log.info("Request to three-ds-server service: request={}", request.toString());

        ResponseEntity<Message> response = restTemplate.postForEntity(url, request, Message.class);

        if (response.getBody() instanceof RBKMoneyPreparationResponse) {
            RBKMoneyPreparationResponse rbkMoneyPreparationResponse = (RBKMoneyPreparationResponse) response.getBody();
            log.info(
                    "Response from three-ds-server service: providerId={}, cardRanges={}",
                    rbkMoneyPreparationResponse.getProviderId(),
                    rbkMoneyPreparationResponse.getCardRanges().size());
            return rbkMoneyPreparationResponse;
        } else {
            throw new MessageTypeException(
                    Optional.ofNullable(response.getBody())
                            .map(Message::toString)
                            .orElse("Message body is empty"));
        }
    }
}

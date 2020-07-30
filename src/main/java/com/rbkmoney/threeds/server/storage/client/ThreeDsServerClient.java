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

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreeDsServerClient {

    private final RestTemplate restTemplate;

    @Value("${client.three-ds-server.url}")
    private String url;

    //    @Retryable(
//            value = {
//                    RestClientResponseException.class,
//                    MessageTypeException.class
//            },
//            backoff = @Backoff(delayExpression = "#{${client.retry.delay-ms}}"),
//            maxAttemptsExpression = "#{${client.retry.max-attempts}}")
    public RBKMoneyPreparationResponse preparationFlow(RBKMoneyPreparationRequest request) {
        log.info("Request: {}", request);

        ResponseEntity<Message> response = restTemplate.postForEntity(url, request, Message.class);

        if (response.getBody() instanceof RBKMoneyPreparationResponse) {
            log.info("Response: {}", response.getBody());
            return (RBKMoneyPreparationResponse) response.getBody();
        } else {
            throw new MessageTypeException(response.getBody().toString());
        }
    }
}

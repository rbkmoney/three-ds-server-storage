package com.rbkmoney.threeds.server.storage.client;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
@EnableRetry
@RequiredArgsConstructor
public class ThreeDsServerClient {

    private final RestTemplate restTemplate;

    @Value("${client.three-ds-server.url}")
    private String url;

    @Retryable(
            value = RestClientResponseException.class,
            backoff = @Backoff(delay = 60_000L),
            maxAttempts = Integer.MAX_VALUE)
    public RBKMoneyPreparationResponse preparationFlow(RBKMoneyPreparationRequest request) {
        return restTemplate.postForEntity(
                url,
                request,
                RBKMoneyPreparationResponse.class)
                .getBody();
    }
}

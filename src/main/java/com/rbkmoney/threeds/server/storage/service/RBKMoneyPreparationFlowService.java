package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.client.ThreeDsServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import javax.transaction.Transactional;

@Service
@EnableRetry
@RequiredArgsConstructor
public class RBKMoneyPreparationFlowService {

    private final ThreeDsServerClient client;
    private final SerialNumUpdater serialNumUpdater;
    private final CardRangeUpdater cardRangeUpdater;

    @Transactional
    @Retryable(
            value = RestClientResponseException.class,
            backoff = @Backoff(delay = 60_000L),
            maxAttempts = Integer.MAX_VALUE)
    public void init(String providerId) {
        String serialNum = serialNumUpdater.getCurrent(providerId);

        RBKMoneyPreparationRequest request = RBKMoneyPreparationRequest.builder()
                .providerId(providerId)
                .serialNum(serialNum)
                .build();

        RBKMoneyPreparationResponse response = client.preparationFlow(request);

        serialNumUpdater.update(response);
        cardRangeUpdater.update(response);
    }
}

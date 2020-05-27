package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.client.ThreeDsServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Service;

@Service
@EnableRetry
@RequiredArgsConstructor
public class PreparationFlowService {

    private final ThreeDsServerClient client;
    private final PreparationFlowDataUpdater dataUpdater;

    public void init(String providerId) {
        String serialNum = dataUpdater.getCurrentSerialNum(providerId);

        RBKMoneyPreparationRequest request = RBKMoneyPreparationRequest.builder()
                .providerId(providerId)
                .serialNum(serialNum)
                .build();

        RBKMoneyPreparationResponse response = client.preparationFlow(request);

        dataUpdater.update(response);
    }
}

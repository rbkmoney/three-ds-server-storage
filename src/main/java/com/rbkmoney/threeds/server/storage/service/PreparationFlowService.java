package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.client.ThreeDsServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreparationFlowService {

    private final ThreeDsServerClient client;
    private final PreparationFlowDataUpdater dataUpdater;

    @Async
    public void init(String providerId, String messageVersion) {
        String serialNum = dataUpdater.getCurrentSerialNum(providerId);

        RBKMoneyPreparationResponse response = preparationFlow(providerId, serialNum, messageVersion);

        dataUpdater.update(response);
    }

    private RBKMoneyPreparationResponse preparationFlow(String providerId, String serialNum, String messageVersion) {
        RBKMoneyPreparationRequest request = RBKMoneyPreparationRequest.builder()
                .providerId(providerId)
                .serialNum(serialNum)
                .build();
        request.setMessageVersion(messageVersion);

        return client.preparationFlow(request);
    }
}

package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.client.ThreeDsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreparationFlowService {

    private final SerialNumService serialNumService;
    private final ThreeDsClient threeDsClient;
    private final PreparationFlowDataUpdater dataUpdater;

    @Async
    public void init(String providerId, String messageVersion) {
        String serialNum = serialNumService.get(providerId).orElse(null);

        RBKMoneyPreparationResponse rbkMoneyPreparationResponse = threeDsClient.preparationFlow(providerId, messageVersion, serialNum);

        dataUpdater.update(rbkMoneyPreparationResponse);
    }
}

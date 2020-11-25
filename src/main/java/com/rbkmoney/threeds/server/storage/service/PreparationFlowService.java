package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.error.ErrorCode;
import com.rbkmoney.threeds.server.domain.root.Message;
import com.rbkmoney.threeds.server.domain.root.emvco.ErroWrapper;
import com.rbkmoney.threeds.server.storage.client.ThreeDsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreparationFlowService {

    private final CardRangeService cardRangeService;
    private final SerialNumberService serialNumberService;
    private final LastUpdatedService lastUpdatedService;
    private final ThreeDsClient threeDsClient;

    @Async
    public void init(String providerId, String messageVersion) {
        String serialNumber = serialNumberService.get(providerId).orElse(null);

        Message message = threeDsClient.preparationFlow(providerId, messageVersion, serialNumber);

        if (message instanceof ErroWrapper) {
            ErroWrapper erroWrapper = (ErroWrapper) message;
            ErrorCode errorCode = erroWrapper.getErrorCode().getValue();
            if (errorCode != ErrorCode.SENT_MESSAGE_LIMIT_EXCEEDED_103) {
                cardRangeService.deleteAll(providerId);
                serialNumberService.delete(providerId);
                lastUpdatedService.delete(providerId);
            }
        }
    }
}

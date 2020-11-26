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

    private final SerialNumberService serialNumberService;
    private final ThreeDsClient threeDsClient;

    @Async
    public void init(String providerId, String messageVersion) {
        String serialNumber = serialNumberService.get(providerId).orElse(null);

        Message message = threeDsClient.preparationFlow(providerId, messageVersion, serialNumber);

        if (message instanceof ErroWrapper) {
            ErroWrapper erroWrapper = (ErroWrapper) message;
            ErrorCode errorCode = erroWrapper.getErrorCode().getValue();
            if (errorCode != ErrorCode.SENT_MESSAGE_LIMIT_EXCEEDED_103) {
                // при любой ошибке формируем требование к следующему запросу на полное обновление диапазонов
                // при этом временно остается текущая схема с карточными диапазонами, уже записанными в базу
                serialNumberService.delete(providerId);
            }
        }
    }
}

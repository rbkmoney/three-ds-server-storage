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
        log.info("Start 'Initialization PreparationFlow', providerId={}", providerId);

        String serialNumber = serialNumberService.get(providerId).orElse(null);

        var response = threeDsClient.preparationFlow(providerId, messageVersion, serialNumber);

        if (response.isPresent()) {
            handleResponse(providerId, response.get());
        } else {
            log.warn("Response message is null, providerId={}", providerId);
        }

        log.info("Finish 'Initialization PreparationFlow', providerId={}", providerId);
    }

    private void handleResponse(String providerId, Message message) {
        if (message instanceof ErroWrapper) {
            ErrorCode errorCode = ((ErroWrapper) message).getErrorCode().getValue();

            // при любой ошибке формируем требование к следующему запросу на полное обновление диапазонов
            // при этом временно остается текущая схема с карточными диапазонами, уже записанными в базу
            if (errorCode != ErrorCode.SENT_MESSAGE_LIMIT_EXCEEDED_103) {
                log.warn("Response message contains ERROR - SerialNumber should be deleted, " +
                        "in next 'Initialization PreparationFlow' service will cleanse all tables " +
                        "and load fresh CardRanges, providerId={}, response={}", providerId, message.toString());

                serialNumberService.delete(providerId);
            } else {
                log.info("Response message contains ERROR — SKIP IT, BECAUSE request can be sent once an hour, " +
                        "providerId={}, response={}", providerId, message.toString());
            }
        } else {
            log.info("Response message is OK — SKIP IT, BECAUSE all is well providerId={}, response={}",
                    providerId,
                    message.toString());
        }
    }
}

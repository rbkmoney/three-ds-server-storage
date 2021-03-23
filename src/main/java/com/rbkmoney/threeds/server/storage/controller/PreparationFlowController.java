package com.rbkmoney.threeds.server.storage.controller;

import com.rbkmoney.threeds.server.storage.service.PreparationFlowService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "rest-handler.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class PreparationFlowController {

    private final PreparationFlowService preparationFlowService;

    @PostMapping("/preparation")
    @ResponseStatus(HttpStatus.OK)
    public void preparationHandler(@RequestBody PreparationRequest request) {
        log.info("Init RBKMoney preparation flow, providerId={}", request.getProviderId());

        preparationFlowService.init(request.getProviderId(), request.getMessageVersion());
    }

    @Data
    public static class PreparationRequest {

        private String providerId;
        private String messageVersion;

    }
}

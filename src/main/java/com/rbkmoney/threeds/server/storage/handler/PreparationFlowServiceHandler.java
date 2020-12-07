package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.threeds.server.storage.InitRBKMoneyPreparationFlowRequest;
import com.rbkmoney.damsel.threeds.server.storage.PreparationFlowInitializerSrv;
import com.rbkmoney.threeds.server.storage.service.PreparationFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreparationFlowServiceHandler implements PreparationFlowInitializerSrv.Iface {

    private final PreparationFlowService preparationFlowService;

    @Override
    public void initRBKMoneyPreparationFlow(InitRBKMoneyPreparationFlowRequest request) {
        log.info("Init RBKMoney preparation flow, providerId={}", request.getProviderId());

        preparationFlowService.init(request.getProviderId(), request.getMessageVersion());
    }
}

package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.InitRBKMoneyPreparationFlowRequest;
import com.rbkmoney.damsel.three_ds_server_storage.PreparationFlowInitializerSrv;
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
        log.info("Init RBK.money preparation flow for providerId={}", request.getProviderId());
        preparationFlowService.init(request.getProviderId(), request.getMessageVersion());
    }
}

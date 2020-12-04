package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfoNotFound;
import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfoStorageSrv;
import com.rbkmoney.threeds.server.storage.service.ChallengeFlowTransactionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeFlowTransactionInfoStorageHandler implements ChallengeFlowTransactionInfoStorageSrv.Iface {

    private final ChallengeFlowTransactionInfoService challengeFlowTransactionInfoService;

    @Override
    public void saveChallengeFlowTransactionInfo(ChallengeFlowTransactionInfo transactionInfo) {
        challengeFlowTransactionInfoService.save(transactionInfo);
    }

    @Override
    public ChallengeFlowTransactionInfo getChallengeFlowTransactionInfo(String transactionId) throws ChallengeFlowTransactionInfoNotFound {
        return challengeFlowTransactionInfoService.get(transactionId)
                .orElseThrow(ChallengeFlowTransactionInfoNotFound::new);
    }
}

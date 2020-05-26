package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfoStorageSrv;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.repository.ChallengeFlowTransactionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class ChallengeFlowTransactionInfoStorageHandler implements ChallengeFlowTransactionInfoStorageSrv.Iface {

    private final ChallengeFlowTransactionInfoRepository repository;

    // TODO [a.romanov]: mappers

    @Override
    public void saveChallengeFlowTransactionInfo(ChallengeFlowTransactionInfo transactionInfo) {
        ChallengeFlowTransactionInfoEntity entity = ChallengeFlowTransactionInfoEntity.builder()
                .id(transactionInfo.getTransactionId())
                .acsDecConInd(transactionInfo.getAcsDecConInd())
                .deviceChannel(transactionInfo.getDeviceChannel())
                .decoupledAuthMaxTime(Date.valueOf(transactionInfo.getDecoupledAuthMaxTime()))
                .build();

        repository.save(entity);
    }

    @Override
    public ChallengeFlowTransactionInfo getChallengeFlowTransactionInfo(String transactionId) {
        ChallengeFlowTransactionInfoEntity entity = repository.getOne(transactionId);

        return new ChallengeFlowTransactionInfo()
                .setTransactionId(transactionId)
                .setAcsDecConInd(entity.getAcsDecConInd())
                .setDeviceChannel(entity.getDeviceChannel())
                .setDecoupledAuthMaxTime(entity.getDecoupledAuthMaxTime().toString());
    }
}

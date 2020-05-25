package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.RReqTransactionInfo;
import com.rbkmoney.damsel.three_ds_server_storage.RReqTransactionInfoStorageSrv;
import com.rbkmoney.threeds.server.storage.entity.RReqTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.repository.RReqTransactionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class RReqTransactionInfoStorageHandler implements RReqTransactionInfoStorageSrv.Iface {

    private final RReqTransactionInfoRepository repository;

    // TODO [a.romanov]: mappers

    @Override
    public void saveRReqTransactionInfo(RReqTransactionInfo transactionInfo) {
        RReqTransactionInfoEntity entity = RReqTransactionInfoEntity.builder()
                .id(transactionInfo.getTransactionId())
                .acsDecConInd(transactionInfo.getAcsDecConInd())
                .deviceChannel(transactionInfo.getDeviceChannel())
                .decoupledAuthMaxTime(Date.valueOf(transactionInfo.getDecoupledAuthMaxTime()))
                .build();

        repository.save(entity);
    }

    @Override
    public RReqTransactionInfo getRReqTransactionInfo(String transactionId) {
        RReqTransactionInfoEntity entity = repository.getOne(transactionId);

        return new RReqTransactionInfo()
                .setTransactionId(transactionId)
                .setAcsDecConInd(entity.getAcsDecConInd())
                .setDeviceChannel(entity.getDeviceChannel())
                .setDecoupledAuthMaxTime(entity.getDecoupledAuthMaxTime().toString());
    }
}

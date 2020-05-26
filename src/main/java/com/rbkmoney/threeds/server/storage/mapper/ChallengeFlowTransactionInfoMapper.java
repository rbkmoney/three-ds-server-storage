package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class ChallengeFlowTransactionInfoMapper {

    public ChallengeFlowTransactionInfoEntity toEntity(ChallengeFlowTransactionInfo domain) {
        return ChallengeFlowTransactionInfoEntity.builder()
                .id(domain.getTransactionId())
                .acsDecConInd(domain.getAcsDecConInd())
                .deviceChannel(domain.getDeviceChannel())
                .decoupledAuthMaxTime(Date.valueOf(domain.getDecoupledAuthMaxTime()))
                .build();
    }

    public ChallengeFlowTransactionInfo toDomain(ChallengeFlowTransactionInfoEntity entity) {
        return new ChallengeFlowTransactionInfo()
                .setTransactionId(entity.getId())
                .setAcsDecConInd(entity.getAcsDecConInd())
                .setDeviceChannel(entity.getDeviceChannel())
                .setDecoupledAuthMaxTime(entity.getDecoupledAuthMaxTime().toString());
    }
}
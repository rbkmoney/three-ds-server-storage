package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChallengeFlowTransactionInfoMapper {

    public ChallengeFlowTransactionInfoEntity fromThriftToEntity(ChallengeFlowTransactionInfo domain) {
        return ChallengeFlowTransactionInfoEntity.builder()
                .transactionId(domain.getTransactionId())
                .deviceChannel(domain.getDeviceChannel())
                .decoupledAuthMaxTime(LocalDateTime.parse(domain.getDecoupledAuthMaxTime()))
                .acsDecConInd(domain.getAcsDecConInd())
                .providerId(domain.getProviderId())
                .messageVersion(domain.getMessageVersion())
                .acsUrl(domain.getAcsUrl())
                .eci(domain.getEci())
                .authenticationValue(domain.getAuthenticationValue())
                .build();
    }

    public ChallengeFlowTransactionInfo fromEntityToThrift(ChallengeFlowTransactionInfoEntity entity) {
        return new ChallengeFlowTransactionInfo()
                .setTransactionId(entity.getTransactionId())
                .setDeviceChannel(entity.getDeviceChannel())
                .setDecoupledAuthMaxTime(entity.getDecoupledAuthMaxTime().toString())
                .setAcsDecConInd(entity.getAcsDecConInd())
                .setProviderId(entity.getProviderId())
                .setMessageVersion(entity.getMessageVersion())
                .setAcsUrl(entity.getAcsUrl())
                .setEci(entity.getEci())
                .setAuthenticationValue(entity.getAuthenticationValue());
    }
}

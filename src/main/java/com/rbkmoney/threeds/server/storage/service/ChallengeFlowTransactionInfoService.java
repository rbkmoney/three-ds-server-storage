package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.mapper.ChallengeFlowTransactionInfoMapper;
import com.rbkmoney.threeds.server.storage.repository.ChallengeFlowTransactionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeFlowTransactionInfoService {

    private final ChallengeFlowTransactionInfoRepository repository;
    private final ChallengeFlowTransactionInfoMapper mapper;

    public void save(ChallengeFlowTransactionInfo transactionInfo) {
        ChallengeFlowTransactionInfoEntity entity = mapper.fromThriftToEntity(transactionInfo);

        log.info(
                "Trying to save ChallengeFlowTransactionInfo, providerId={}, transactionId={}",
                entity.getProviderId(),
                entity.getTransactionId());

        repository.save(entity);

        log.info(
                "ChallengeFlowTransactionInfo has been saved, providerId={}, transactionId={}",
                entity.getProviderId(),
                entity.getTransactionId());
    }

    public Optional<ChallengeFlowTransactionInfo> get(String transactionId) {
        log.info("Trying to get ChallengeFlowTransactionInfo, transactionId={}", transactionId);

        Optional<ChallengeFlowTransactionInfo> transactionInfo = repository.findByTransactionId(transactionId)
                .map(mapper::fromEntityToThrift);

        log.info("getChallengeFlowTransactionInfo={}, transactionId={}", transactionInfo.toString(), transactionId);

        return transactionInfo;
    }
}

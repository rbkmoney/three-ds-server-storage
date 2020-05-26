package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfoStorageSrv;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.mapper.ChallengeFlowTransactionInfoMapper;
import com.rbkmoney.threeds.server.storage.repository.ChallengeFlowTransactionInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeFlowTransactionInfoStorageHandler implements ChallengeFlowTransactionInfoStorageSrv.Iface {

    private final ChallengeFlowTransactionInfoRepository repository;
    private final ChallengeFlowTransactionInfoMapper mapper;

    @Override
    public void saveChallengeFlowTransactionInfo(ChallengeFlowTransactionInfo transactionInfo) {
        ChallengeFlowTransactionInfoEntity entity = mapper.toEntity(transactionInfo);
        repository.save(entity);
    }

    @Override
    public ChallengeFlowTransactionInfo getChallengeFlowTransactionInfo(String transactionId) {
        ChallengeFlowTransactionInfoEntity entity = repository.getOne(transactionId);
        return mapper.toDomain(entity);
    }
}

package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.damsel.three_ds_server_storage.ChallengeFlowTransactionInfoStorageSrv;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.exception.ChallengeFlowTransactionInfoNotFoundException;
import com.rbkmoney.threeds.server.storage.mapper.ChallengeFlowTransactionInfoMapper;
import com.rbkmoney.threeds.server.storage.repository.ChallengeFlowTransactionInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeFlowTransactionInfoStorageHandler implements ChallengeFlowTransactionInfoStorageSrv.Iface {

    private final ChallengeFlowTransactionInfoRepository repository;
    private final ChallengeFlowTransactionInfoMapper mapper;

    @Override
    public void saveChallengeFlowTransactionInfo(ChallengeFlowTransactionInfo transactionInfo) {
        ChallengeFlowTransactionInfoEntity entity = mapper.toEntity(transactionInfo);

        log.debug("Save challengeFlowTransactionInfo with transactionId={}", transactionInfo.getTransactionId());
        repository.save(entity);
    }

    @Override
    public ChallengeFlowTransactionInfo getChallengeFlowTransactionInfo(String transactionId) {
        log.debug("Return challengeFlowTransactionInfo with transactionId={}", transactionId);
        return repository.findByTransactionId(transactionId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new ChallengeFlowTransactionInfoNotFoundException("transactionId=" + transactionId));
    }
}

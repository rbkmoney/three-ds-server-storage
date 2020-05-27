package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SerialNumUpdater {

    private final SerialNumRepository repository;

    public String getCurrent(String providerId) {
        return repository
                .findByProviderId(providerId)
                .map(SerialNumEntity::getSerialNum)
                .orElse(null);
    }

    public void update(RBKMoneyPreparationResponse response) {
        String updatedSerialNum = response.getSerialNum();
        if (updatedSerialNum == null) return;

        repository.save(SerialNumEntity.builder()
                .providerId(response.getProviderId())
                .serialNum(updatedSerialNum)
                .build());
    }
}

package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.mapper.SerialNumMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PreparationFlowDataUpdater {

    private final SerialNumRepository serialNumRepository;
    private final SerialNumMapper serialNumMapper;
    private final CardRangeRepository cardRangeRepository;
    private final CardRangeMapper cardRangeMapper;

    public String getCurrentSerialNum(String providerId) {
        return serialNumRepository
                .findByProviderId(providerId)
                .map(SerialNumEntity::getSerialNum)
                .orElse(null);
    }

    @Transactional
    public void update(RBKMoneyPreparationResponse response) {
        String providerId = response.getProviderId();
        String serialNum = response.getSerialNum();

        cardRangeRepository.saveAll(cardRangeMapper.toEntities(response.getAddedCardRanges(), providerId));
        cardRangeRepository.saveAll(cardRangeMapper.toEntities(response.getModifiedCardRanges(), providerId));
        cardRangeRepository.deleteAll(cardRangeMapper.toEntities(response.getDeletedCardRanges(), providerId));
        serialNumRepository.save(serialNumMapper.toEntity(serialNum, providerId));
    }
}

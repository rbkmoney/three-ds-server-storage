package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.dto.CardRangeDTO;
import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.mapper.SerialNumMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
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
        List<CardRangeDTO> addedCardRanges = response.getAddedCardRanges();
        List<CardRangeDTO> modifiedCardRanges = response.getModifiedCardRanges();
        List<CardRangeDTO> deletedCardRanges = response.getDeletedCardRanges();

        log.info("Update preparation flow data for providerId={}: " +
                        "serialNum={}, cardRanges=[added={}, modified={}, deleted={}]",
                providerId,
                serialNum,
                addedCardRanges.size(),
                modifiedCardRanges.size(),
                deletedCardRanges.size());

        cardRangeRepository.saveAll(cardRangeMapper.toEntities(addedCardRanges, providerId));
        cardRangeRepository.saveAll(cardRangeMapper.toEntities(modifiedCardRanges, providerId));
        cardRangeRepository.deleteAll(cardRangeMapper.toEntities(deletedCardRanges, providerId));
        serialNumRepository.save(serialNumMapper.toEntity(serialNum, providerId));
    }
}

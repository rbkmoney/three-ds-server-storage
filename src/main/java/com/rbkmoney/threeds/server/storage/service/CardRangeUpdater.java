package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardRangeUpdater {

    private final CardRangeRepository repository;
    private final CardRangeMapper mapper;

    public void update(RBKMoneyPreparationResponse response) {
        String providerId = response.getProviderId();

        repository.saveAll(mapper.toEntities(response.getAddedCardRanges(), providerId));
        repository.saveAll(mapper.toEntities(response.getModifiedCardRanges(), providerId));
        repository.deleteAll(mapper.toEntities(response.getDeletedCardRanges(), providerId));
    }
}

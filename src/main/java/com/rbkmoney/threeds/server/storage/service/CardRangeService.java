package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.cardrange.ActionInd;
import com.rbkmoney.threeds.server.domain.rbkmoney.cardrange.RBKMoneyCardRange;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.rbkmoney.threeds.server.domain.cardrange.ActionInd.*;

@Service
@RequiredArgsConstructor
public class CardRangeService {

    private final CardRangeRepository cardRangeRepository;
    private final CardRangeMapper cardRangeMapper;

    public boolean doesNotExistsCardRanges(String providerId) {
        return !cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(providerId);
    }

    public boolean existsFreeSpaceForNewCardRange(String providerId, long startRange, long endRange) {
        return cardRangeRepository.existsFreeSpaceForNewCardRange(providerId, startRange, endRange);
    }

    public boolean existsCardRange(String providerId, long startRange, long endRange) {
        CardRangePk pk = CardRangePk.builder()
                .providerId(providerId)
                .rangeStart(startRange)
                .rangeEnd(endRange)
                .build();
        return cardRangeRepository.existsCardRangeEntityByPkEquals(pk);
    }

    public boolean isInCardRange(String providerId, long accountNumber) {
        return cardRangeRepository.existsCardRangeEntityByPkProviderIdIsAndPkRangeStartLessThanEqualAndPkRangeEndGreaterThanEqual(providerId, accountNumber, accountNumber);
    }

    @Transactional
    public void saveAll(String providerId, List<RBKMoneyCardRange> domainCardRanges) {
        List<CardRangeEntity> entityCardRanges = new ArrayList<>();

        Iterator<RBKMoneyCardRange> iterator = domainCardRanges.iterator();

        while (iterator.hasNext()) {
            RBKMoneyCardRange domainCardRange = iterator.next();
            ActionInd actionInd = domainCardRange.getActionInd();

            var entity = cardRangeMapper.fromJsonToEntity(domainCardRange, providerId);

            if (actionInd == ADD_CARD_RANGE_TO_CACHE
                    || actionInd == MODIFY_CARD_RANGE_DATA) {
                entityCardRanges.add(entity);
                iterator.remove();
            }
        }

        cardRangeRepository.saveAll(entityCardRanges);
    }

    @Transactional
    public void deleteAll(String providerId, List<RBKMoneyCardRange> domainCardRanges) {
        List<CardRangeEntity> deletedEntityCardRanges = new ArrayList<>();

        Iterator<RBKMoneyCardRange> iterator = domainCardRanges.iterator();

        while (iterator.hasNext()) {
            RBKMoneyCardRange domainCardRange = iterator.next();
            ActionInd actionInd = domainCardRange.getActionInd();

            var entity = cardRangeMapper.fromJsonToEntity(domainCardRange, providerId);
            if (actionInd == DELETE_CARD_RANGE_FROM_CACHE) {
                deletedEntityCardRanges.add(entity);
                iterator.remove();
            }
        }

        cardRangeRepository.deleteAll(deletedEntityCardRanges);
    }
}

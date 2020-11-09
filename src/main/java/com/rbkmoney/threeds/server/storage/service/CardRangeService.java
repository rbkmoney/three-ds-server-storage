package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.cardrange.ActionInd;
import com.rbkmoney.threeds.server.domain.rbkmoney.cardrange.RBKMoneyCardRange;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.rbkmoney.threeds.server.domain.cardrange.ActionInd.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardRangeService {

    private final CardRangeRepository cardRangeRepository;
    private final CardRangeMapper cardRangeMapper;

    @Transactional
    public void saveAll(String providerId, List<RBKMoneyCardRange> domainCardRanges) {
        log.info("Trying to save CardRanges, providerId={}, allCardRanges={}", providerId, domainCardRanges.size());

        List<CardRangeEntity> savedCardRanges = new ArrayList<>();

        Iterator<RBKMoneyCardRange> iterator = domainCardRanges.iterator();

        while (iterator.hasNext()) {
            RBKMoneyCardRange domainCardRange = iterator.next();
            ActionInd actionInd = domainCardRange.getActionInd();

            var entity = cardRangeMapper.fromJsonToEntity(domainCardRange, providerId);

            if (actionInd == ADD_CARD_RANGE_TO_CACHE
                    || actionInd == MODIFY_CARD_RANGE_DATA) {
                savedCardRanges.add(entity);
                iterator.remove();
            }
        }

        cardRangeRepository.saveAll(savedCardRanges);

        log.info("CardRanges has been saved, providerId={}, savedCardRanges={}", providerId, savedCardRanges.size());
    }

    @Transactional
    public void deleteAll(String providerId, List<RBKMoneyCardRange> domainCardRanges) {
        log.info("Trying to delete CardRanges, providerId={}, allCardRanges={}", providerId, domainCardRanges.size());

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

        log.info("CardRanges has been deleted, providerId={}, deletedCardRanges={}", providerId, deletedEntityCardRanges.size());
    }

    public boolean doesNotExistsCardRanges(String providerId) {
        boolean doesNotExistsCardRanges = !cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(providerId);

        log.info("doesNotExistsCardRanges={}, providerId={}", doesNotExistsCardRanges, providerId);

        return doesNotExistsCardRanges;
    }

    public boolean existsFreeSpaceForNewCardRange(String providerId, long startRange, long endRange) {
        boolean existsFreeSpaceForNewCardRange = cardRangeRepository.existsFreeSpaceForNewCardRange(providerId, startRange, endRange);

        log.debug(
                "existsFreeSpaceForNewCardRange={}, providerId={}, startRange={}, endRange={}",
                existsFreeSpaceForNewCardRange,
                providerId,
                startRange,
                endRange);

        return existsFreeSpaceForNewCardRange;
    }

    public boolean existsCardRange(String providerId, long startRange, long endRange) {
        CardRangePk cardRangePk = CardRangePk.builder()
                .providerId(providerId)
                .rangeStart(startRange)
                .rangeEnd(endRange)
                .build();

        boolean existsCardRange = cardRangeRepository.existsCardRangeEntityByPkEquals(cardRangePk);

        log.debug(
                "existsCardRange={}, providerId={}, startRange={}, endRange={}",
                existsCardRange,
                providerId,
                startRange,
                endRange);

        return existsCardRange;
    }

    public boolean isInCardRange(String providerId, long accountNumber) {
        boolean isInCardRange = cardRangeRepository.existsCardRangeEntityByPkProviderIdIsAndPkRangeStartLessThanEqualAndPkRangeEndGreaterThanEqual(providerId, accountNumber, accountNumber);

        log.info("isInCardRange={}, providerId={}, accountNumber={}", isInCardRange, providerId, accountNumber);

        return isInCardRange;
    }
}

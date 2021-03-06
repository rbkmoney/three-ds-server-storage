package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.damsel.threeds.server.storage.Action;
import com.rbkmoney.damsel.threeds.server.storage.CardRange;
import com.rbkmoney.damsel.threeds.server.storage.ThreeDsSecondVersion;
import com.rbkmoney.damsel.threeds.server.storage.UpdateCardRangesRequest;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.threeds.server.storage.utils.CardRangeWrapper.toStringHideCardRange;
import static com.rbkmoney.threeds.server.utils.AccountNumberUtils.hideAccountNumber;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardRangeService {

    private final CardRangeRepository cardRangeRepository;
    private final CardRangeMapper cardRangeMapper;
    private final SerialNumberService serialNumberService;
    private final LastUpdatedService lastUpdatedService;

    @Async
    public void update(UpdateCardRangesRequest request) {
        var providerId = request.getProviderId();
        var thriftCardRanges = request.getCardRanges();
        var isNeedStorageClear = request.isIsNeedStorageClear();
        var serialNumber = Optional.ofNullable(request.getSerialNumber());

        log.info(
                "Update CardRanges (during the current 'Initialization PreparationFlow'), " +
                        "providerId={}, isNeedStorageClear={}, serialNumber={}, cardRanges={}",
                providerId,
                isNeedStorageClear,
                serialNumber,
                thriftCardRanges.size());

        if (isNeedStorageClear) {
            deleteByProviderId(providerId);
        } else {
            deleteCardRangeEntities(providerId, thriftCardRanges);
        }

        saveCardRangeEntities(providerId, thriftCardRanges);
        lastUpdatedService.save(providerId);
        if (serialNumber.isPresent()) {
            serialNumberService.save(providerId, serialNumber.get());
        } else {
            serialNumberService.delete(providerId);
        }

        log.info(
                "Finish update CardRanges (during the current 'Initialization PreparationFlow'), " +
                        "providerId={}, isNeedStorageClear={}, serialNumber={}, cardRanges={}",
                providerId,
                isNeedStorageClear,
                serialNumber,
                thriftCardRanges.size());
    }

    public boolean doesNotExistsCardRanges(String providerId) {
        boolean doesNotExistsCardRanges = !cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(providerId);

        log.info("Storage is empty = '{}', providerId={}", doesNotExistsCardRanges, providerId);

        return doesNotExistsCardRanges;
    }

    public boolean existsFreeSpaceForNewCardRange(String providerId, CardRange cardRange) {
        long startRange = cardRange.getRangeStart();
        long endRange = cardRange.getRangeEnd();

        boolean existsFreeSpaceForNewCardRange = cardRangeRepository.existsFreeSpaceForNewCardRange(
                providerId,
                startRange,
                endRange);

        log.debug(
                "CardRange can be added = '{}', providerId={}, cardRange={}",
                existsFreeSpaceForNewCardRange,
                providerId,
                toStringHideCardRange(cardRange));

        return existsFreeSpaceForNewCardRange;
    }

    public boolean existsCardRange(String providerId, CardRange cardRange) {
        var pk = cardRangePk(providerId, cardRange.getRangeStart(), cardRange.getRangeEnd());

        boolean existsCardRange = cardRangeRepository.existsCardRangeEntityByPkEquals(pk);

        log.debug(
                "CardRange can be modified or deleted = '{}', providerId={}, cardRange={}",
                existsCardRange,
                providerId,
                toStringHideCardRange(cardRange));

        return existsCardRange;
    }

    public Optional<String> getProviderId(long accountNumber) {
        var providerId = cardRangeRepository.getProviderIds(accountNumber, limitOne())
                .stream()
                .findFirst();

        log.info("ProviderId by AccountNumber has been found, providerId={}, accountNumber={}",
                providerId.toString(),
                hideAccountNumber(accountNumber));

        return providerId;
    }

    public Optional<ThreeDsSecondVersion> getThreeDsSecondVersion(long accountNumber) {
        return cardRangeRepository.getCardRangeEntities(accountNumber, limitOne())
                .stream()
                .findFirst()
                .map(
                        cardRange -> {
                            log.info("CardRange by AccountNumber has been found, cardRange={}, accountNumber={}",
                                    toStringHideCardRange(cardRange), hideAccountNumber(accountNumber));

                            return Optional.of(cardRangeMapper.fromEntityToThrift(cardRange));
                        })
                .orElseGet(Optional::empty);
    }

    void saveCardRangeEntities(String providerId, List<CardRange> thriftCardRanges) {
        List<CardRangeEntity> savedCardRanges = new ArrayList<>();

        Iterator<CardRange> iterator = thriftCardRanges.iterator();

        while (iterator.hasNext()) {
            CardRange thriftCardRange = iterator.next();
            Action action = thriftCardRange.getAction();

            if (action.isSetAddCardRange() || action.isSetModifyCardRange()) {
                var entity = cardRangeMapper.fromThriftToEntity(thriftCardRange, providerId);
                savedCardRanges.add(entity);
                iterator.remove();
            }
        }

        if (!savedCardRanges.isEmpty()) {
            log.info("Trying to save CardRanges, providerId={}, cardRanges={}", providerId, savedCardRanges.size());

            cardRangeRepository.saveOrUpdateWithPessimisticLocking(providerId, savedCardRanges);

            log.info("CardRanges has been saved, providerId={}, cardRanges={}", providerId, savedCardRanges.size());
        } else {
            log.info("Nothing to save, CardRanges is empty, providerId={}", providerId);
        }
    }

    void deleteCardRangeEntities(String providerId, List<CardRange> thriftCardRanges) {
        List<CardRangeEntity> deletedCardRanges = new ArrayList<>();

        Iterator<CardRange> iterator = thriftCardRanges.iterator();

        while (iterator.hasNext()) {
            CardRange thriftCardRange = iterator.next();
            Action action = thriftCardRange.getAction();

            if (action.isSetDeleteCardRange()) {
                var entity = cardRangeMapper.fromThriftToEntity(thriftCardRange, providerId);
                deletedCardRanges.add(entity);
                iterator.remove();
            }
        }

        if (!deletedCardRanges.isEmpty()) {
            log.info("Trying to delete CardRanges, providerId={}, cardRanges={}", providerId, deletedCardRanges.size());

            cardRangeRepository.deleteAll(deletedCardRanges);

            log.info("CardRanges has been deleted, providerId={}, cardRanges={}", providerId, deletedCardRanges.size());
        } else {
            log.info("Nothing to delete, CardRanges is empty, providerId={}", providerId);
        }
    }

    private void deleteByProviderId(String providerId) {
        log.info("Trying to delete CardRanges by ProviderId, providerId={}", providerId);

        if (!doesNotExistsCardRanges(providerId)) {
            cardRangeRepository.deleteAllByPkProviderId(providerId);
        }
    }

    private CardRangePk cardRangePk(String providerId, long startRange, long endRange) {
        return CardRangePk.builder()
                .providerId(providerId)
                .rangeStart(startRange)
                .rangeEnd(endRange)
                .build();
    }

    private PageRequest limitOne() {
        return PageRequest.of(0, 1);
    }
}

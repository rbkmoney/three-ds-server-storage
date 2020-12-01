package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.damsel.three_ds_server_storage.Action;
import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.damsel.three_ds_server_storage.UpdateCardRangesRequest;
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
        String providerId = request.getProviderId();
        String serialNumber = request.getSerialNumber();
        List<CardRange> tCardRanges = request.getCardRanges();
        boolean isNeedStorageClear = request.isIsNeedStorageClear();

        log.info(
                "[async] Update CardRanges, providerId={}, isNeedStorageClear={}, serialNumber={}, cardRanges={}",
                providerId,
                isNeedStorageClear,
                serialNumber,
                tCardRanges.size());

        if (isNeedStorageClear) {
            deleteAll(providerId);
        } else {
            deleteAll(providerId, tCardRanges);
        }

        saveAll(providerId, tCardRanges);
        lastUpdatedService.save(providerId);
        serialNumberService.save(providerId, serialNumber);

        log.info(
                "[async] Finish update CardRanges, providerId={}, isNeedStorageClear={}, serialNumber={}, cardRanges={}",
                providerId,
                isNeedStorageClear,
                serialNumber,
                tCardRanges.size());
    }

    public boolean doesNotExistsCardRanges(String providerId) {
        boolean doesNotExistsCardRanges = !cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(providerId);

        log.info("doesNotExistsCardRanges={}, providerId={}", doesNotExistsCardRanges, providerId);

        return doesNotExistsCardRanges;
    }

    public boolean existsFreeSpaceForNewCardRange(String providerId, CardRange cardRange) {
        long startRange = cardRange.getRangeStart();
        long endRange = cardRange.getRangeEnd();

        boolean existsFreeSpaceForNewCardRange = cardRangeRepository.existsFreeSpaceForNewCardRange(providerId, startRange, endRange);

        log.debug(
                "existsFreeSpaceForNewCardRange={}, providerId={}, cardRange={}",
                existsFreeSpaceForNewCardRange,
                providerId,
                toStringHideCardRange(cardRange));

        return existsFreeSpaceForNewCardRange;
    }

    public boolean existsCardRange(String providerId, CardRange cardRange) {
        CardRangePk cardRangePk = cardRangePk(providerId, cardRange.getRangeStart(), cardRange.getRangeEnd());

        boolean existsCardRange = cardRangeRepository.existsCardRangeEntityByPkEquals(cardRangePk);

        log.debug(
                "existsCardRange={}, providerId={}, cardRange={}",
                existsCardRange,
                providerId,
                toStringHideCardRange(cardRange));

        return existsCardRange;
    }

    public Optional<String> getProviderId(long accountNumber) {
        log.info("Trying to getProviderId, accountNumber={}", hideAccountNumber(accountNumber));

        Optional<String> providerId = cardRangeRepository.getProviderIds(accountNumber, limitOne())
                .stream()
                .findFirst();

        log.info("getProviderId={}, accountNumber={}", providerId.toString(), hideAccountNumber(accountNumber));

        return providerId;
    }

    void saveAll(String providerId, List<CardRange> tCardRanges) {
        List<CardRangeEntity> savedCardRanges = new ArrayList<>();

        Iterator<CardRange> iterator = tCardRanges.iterator();

        while (iterator.hasNext()) {
            CardRange tCardRange = iterator.next();
            Action action = tCardRange.getAction();

            if (action.isSetAddCardRange() || action.isSetModifyCardRange()) {
                var entity = cardRangeMapper.fromThriftToEntity(tCardRange, providerId);
                savedCardRanges.add(entity);
                iterator.remove();
            }
        }

        if (!savedCardRanges.isEmpty()) {
            log.info("[async] Trying to save CardRanges, providerId={}, cardRanges={}", providerId, savedCardRanges.size());

            cardRangeRepository.saveAll(savedCardRanges);

            log.info("[async] CardRanges has been saved, providerId={}, cardRanges={}", providerId, savedCardRanges.size());
        } else {
            log.info("[async] Nothing to save, CardRanges is empty, providerId={}", providerId);
        }
    }

    void deleteAll(String providerId, List<CardRange> tCardRanges) {
        List<CardRangeEntity> deletedCardRanges = new ArrayList<>();

        Iterator<CardRange> iterator = tCardRanges.iterator();

        while (iterator.hasNext()) {
            CardRange tCardRange = iterator.next();
            Action action = tCardRange.getAction();

            if (action.isSetDeleteCardRange()) {
                var entity = cardRangeMapper.fromThriftToEntity(tCardRange, providerId);
                deletedCardRanges.add(entity);
                iterator.remove();
            }
        }

        if (!deletedCardRanges.isEmpty()) {
            log.info("[async] Trying to delete CardRanges, providerId={}, cardRanges={}", providerId, deletedCardRanges.size());

            cardRangeRepository.deleteAll(deletedCardRanges);

            log.info("[async] CardRanges has been deleted, providerId={}, cardRanges={}", providerId, deletedCardRanges.size());
        } else {
            log.info("[async] Nothing to delete, CardRanges is empty, providerId={}", providerId);
        }
    }

    private void deleteAll(String providerId) {
        log.info("[async] Trying to delete all CardRanges by providerId, providerId={}", providerId);

        cardRangeRepository.deleteAllByPkProviderId(providerId);
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

package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.threeds.server.storage.service.CardRangeService;
import com.rbkmoney.threeds.server.storage.service.LastUpdatedService;
import com.rbkmoney.threeds.server.storage.service.SerialNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.rbkmoney.threeds.server.storage.utils.CardRangeWrapper.toStringHideCardRange;
import static java.util.function.Predicate.not;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final CardRangeService cardRangeService;
    private final SerialNumberService serialNumberService;
    private final LastUpdatedService lastUpdatedService;

    @Override
    public void updateCardRanges(UpdateRBKMoneyPreparationFlowRequest updateRBKMoneyPreparationFlowRequest) {
        String providerId = updateRBKMoneyPreparationFlowRequest.getProviderId();
        String serialNumber = updateRBKMoneyPreparationFlowRequest.getSerialNumber();
        List<CardRange> tCardRanges = updateRBKMoneyPreparationFlowRequest.getCardRanges();

        log.info(
                "Update preparation flow data, providerId={}, serialNumber={}, cardRanges={}",
                providerId,
                serialNumber,
                tCardRanges.size());

        cardRangeService.deleteAll(providerId, tCardRanges);
        cardRangeService.saveAll(providerId, tCardRanges);
        lastUpdatedService.save(providerId);
        serialNumberService.save(providerId, serialNumber);

        log.info(
                "Finish update preparation flow data, providerId={}, serialNumber={}, cardRanges={}",
                providerId,
                serialNumber,
                tCardRanges.size());
    }

    @Override
    public boolean isStorageEmpty(String providerId) {
        return cardRangeService.doesNotExistsCardRanges(providerId);
    }

    @Override
    public boolean isValidCardRanges(String providerId, List<CardRange> cardRanges) {
        if (isStorageEmpty(providerId)) {
            return true;
        }

        Optional<CardRange> invalidCardRange = cardRanges.stream()
                .filter(not(cr -> isValidCardRange(providerId, cr)))
                .findFirst();

        if (invalidCardRange.isPresent()) {
            log.warn(
                    "CardRanges is invalid, providerId={}, cardRange={}",
                    providerId,
                    toStringHideCardRange(invalidCardRange.get()));
        } else {
            log.info("CardRanges is valid, providerId={}, cardRanges={}", providerId, cardRanges.size());
        }

        return invalidCardRange.isEmpty();
    }

    @Override
    public String getDirectoryServerProviderId(long accountNumber) throws DirectoryServerProviderIDNotFound {
        return cardRangeService.getProviderId(accountNumber)
                .orElseThrow(DirectoryServerProviderIDNotFound::new);
    }

    private boolean isValidCardRange(String providerId, CardRange cardRange) {
        Action action = cardRange.getAction();

        if (action.isSetAddCardRange()) {
            if (cardRangeService.existsCardRange(providerId, cardRange)) {
                return true;
            }
            return cardRangeService.existsFreeSpaceForNewCardRange(providerId, cardRange);
        } else if (action.isSetModifyCardRange() || action.isSetDeleteCardRange()) {
            return cardRangeService.existsCardRange(providerId, cardRange);
        }

        throw new IllegalArgumentException(String.format("Action Indicator missing in CardRange, cardRange=%s", toStringHideCardRange(cardRange)));
    }
}

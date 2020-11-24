package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.Action;
import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.damsel.three_ds_server_storage.CardRangesStorageSrv;
import com.rbkmoney.damsel.three_ds_server_storage.DirectoryServerProviderIDNotFound;
import com.rbkmoney.threeds.server.storage.service.CardRangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final CardRangeService cardRangeService;

    @Override
    public boolean isValidCardRanges(String providerId, List<CardRange> cardRanges) {
        if (cardRangeService.doesNotExistsCardRanges(providerId)) {
            return true;
        }

        Optional<CardRange> cardRange = cardRanges.stream()
                .filter(not(cr -> isValidCardRange(providerId, cr)))
                .findFirst();

        if (cardRange.isPresent()) {
            log.warn("Exist invalid CardRange, check finished, cardRange={}", cardRange.toString());
        } else {
            log.info("CardRanges is valid, providerId={}, cardRanges={}", providerId, cardRanges.size());
        }

        return cardRange.isEmpty();
    }

    @Override
    public String getDirectoryServerProviderId(long accountNumber) throws DirectoryServerProviderIDNotFound {
        return cardRangeService.getProviderId(accountNumber)
                .orElseThrow(DirectoryServerProviderIDNotFound::new);
    }

    private boolean isValidCardRange(String providerId, CardRange cardRange) {
        long startRange = cardRange.getRangeStart();
        long endRange = cardRange.getRangeEnd();
        Action action = cardRange.getAction();

        if (action.isSetAddCardRange()) {
            if (cardRangeService.existsCardRange(providerId, startRange, endRange)) {
                return true;
            }
            return cardRangeService.existsFreeSpaceForNewCardRange(providerId, startRange, endRange);
        } else if (action.isSetModifyCardRange() || action.isSetDeleteCardRange()) {
            return cardRangeService.existsCardRange(providerId, startRange, endRange);
        }

        throw new IllegalArgumentException(String.format("Action Indicator missing in Card Range Data, cardRange=%s", cardRange));
    }
}

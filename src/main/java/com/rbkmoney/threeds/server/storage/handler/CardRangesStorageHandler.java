package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.Action;
import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.damsel.three_ds_server_storage.CardRangesStorageSrv;
import com.rbkmoney.threeds.server.storage.service.CardRangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final CardRangeService cardRangeService;

    @Override
    public boolean isStorageEmpty(String providerId) throws TException {
        return cardRangeService.doesNotExistsCardRanges(providerId);
    }

    @Override
    public boolean isValidCardRanges(String providerId, List<CardRange> cardRanges) throws TException {
        boolean isValidCardRanges = cardRanges.stream()
                .allMatch(cardRange -> isValidCardRange(providerId, cardRange));

        log.info("isValidCardRanges={}, providerId={}, cardRanges={}", isValidCardRanges, providerId, cardRanges.size());

        return isValidCardRanges;
    }

    @Override
    public boolean isInCardRange(String providerId, long accountNumber) throws TException {
        return cardRangeService.isInCardRange(providerId, accountNumber);
    }

    private boolean isValidCardRange(String providerId, CardRange cardRange) {
        long startRange = cardRange.getRangeStart();
        long endRange = cardRange.getRangeEnd();
        Action action = cardRange.getAction();

        if (action.isSetAddCardRange()) {
            return cardRangeService.existsFreeSpaceForNewCardRange(providerId, startRange, endRange);
        } else if (action.isSetModifyCardRange() || action.isSetDeleteCardRange()) {
            return cardRangeService.existsCardRange(providerId, startRange, endRange);
        }

        throw new IllegalArgumentException(String.format("Action Indicator missing in Card Range Data, cardRange=%s", cardRange));
    }
}

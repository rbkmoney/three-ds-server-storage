package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.threeds.server.storage.AccountNumberVersion;
import com.rbkmoney.damsel.threeds.server.storage.Action;
import com.rbkmoney.damsel.threeds.server.storage.CardRange;
import com.rbkmoney.damsel.threeds.server.storage.CardRangesStorageSrv;
import com.rbkmoney.damsel.threeds.server.storage.DirectoryServerProviderIDNotFound;
import com.rbkmoney.damsel.threeds.server.storage.UnsupportedVersion;
import com.rbkmoney.damsel.threeds.server.storage.UpdateCardRangesRequest;
import com.rbkmoney.threeds.server.storage.service.CardRangeService;
import com.rbkmoney.threeds.server.storage.utils.CardRangeWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.threeds.server.storage.utils.CardRangeWrapper.toStringHideCardRange;
import static java.util.function.Predicate.not;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final CardRangeService cardRangeService;

    @Override
    public void updateCardRanges(UpdateCardRangesRequest request) {
        cardRangeService.update(request);
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

        List<CardRange> invalidCardRanges = cardRanges.stream()
                .filter(not(cr -> isValidCardRange(providerId, cr)))
                .collect(Collectors.toList());

        if (!invalidCardRanges.isEmpty()) {
            String hideCardRanges = invalidCardRanges.stream()
                    .map(CardRangeWrapper::toStringHideCardRange)
                    .collect(Collectors.joining(", ", "[", "]"));

            log.warn("CardRanges is invalid, providerId={}, cardRanges={}", providerId, hideCardRanges);
        } else {
            log.info("CardRanges is valid, providerId={}, cardRanges={}", providerId, cardRanges.size());
        }

        return invalidCardRanges.isEmpty();
    }

    @Override
    public String getDirectoryServerProviderId(long accountNumber) throws DirectoryServerProviderIDNotFound {
        return cardRangeService.getProviderId(accountNumber)
                .orElseThrow(DirectoryServerProviderIDNotFound::new);
    }

    @Override
    public AccountNumberVersion getAccountNumberVersion(long accountNumber) {
        return cardRangeService.getThreeDsSecondVersion(accountNumber)
                .map(AccountNumberVersion::three_ds_second_version)
                .orElseGet(() -> AccountNumberVersion.unsupported_version(new UnsupportedVersion()));
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

        throw new IllegalArgumentException(String.format(
                "Action Indicator missing in CardRange, cardRange=%s",
                toStringHideCardRange(cardRange)));
    }
}

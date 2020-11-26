package com.rbkmoney.threeds.server.storage.utils;

import com.rbkmoney.damsel.three_ds_server_storage.CardRange;

import static com.rbkmoney.threeds.server.utils.AccountNumberUtils.hideAccountNumber;

public class CardRangeWrapper {

    public static String toStringHideCardRange(CardRange cardRange) {
        return HideCardRange.builder()
                .startRange(hideAccountNumber(cardRange.getRangeStart()))
                .endRange(hideAccountNumber(cardRange.getRangeEnd()))
                .action(cardRange.getAction())
                .build()
                .toString();
    }
}

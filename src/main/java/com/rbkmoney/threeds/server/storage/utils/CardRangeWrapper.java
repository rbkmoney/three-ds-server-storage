package com.rbkmoney.threeds.server.storage.utils;

import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;

import java.net.MalformedURLException;
import java.net.URL;

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

    public static String toStringHideCardRange(CardRangeEntity cardRange) {
        return HideCardRange.builder()
                .startRange(hideAccountNumber(cardRange.getPk().getRangeStart()))
                .endRange(hideAccountNumber(cardRange.getPk().getRangeEnd()))
                .acsStartProtocolVersion(cardRange.getAcsStartProtocolVersion())
                .acsEndProtocolVersion(cardRange.getAcsEndProtocolVersion())
                .dsStartProtocolVersion(cardRange.getDsStartProtocolVersion())
                .dsEndProtocolVersion(cardRange.getDsEndProtocolVersion())
                .acsInformationIndicator(cardRange.getAcsInformationIndicator())
                .threeDsMethodUrl(getThreeDsMethodUrl(cardRange))
                .build()
                .toString();
    }

    private static String getThreeDsMethodUrl(CardRangeEntity cardRange) {
        try {
            return new URL(cardRange.getThreeDsMethodUrl()).getPath();
        } catch (MalformedURLException e) {
            return null;
        }
    }
}

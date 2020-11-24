package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.threeds.server.domain.rbkmoney.cardrange.RBKMoneyCardRange;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.springframework.stereotype.Service;

import static java.lang.Long.parseLong;

@Service
public class CardRangeMapper {

    public CardRangeEntity fromJsonToEntity(RBKMoneyCardRange cardRange, String providerId) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(parseLong(cardRange.getStartRange()))
                        .rangeEnd(parseLong(cardRange.getEndRange()))
                        .build())
                .threeDsMethodUrl(cardRange.getThreeDSMethodURL())
                .build();
    }
}

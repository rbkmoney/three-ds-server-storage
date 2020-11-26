package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.springframework.stereotype.Service;

@Service
public class CardRangeMapper {

    public CardRangeEntity fromThriftToEntity(CardRange cardRange, String providerId) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(cardRange.getRangeStart())
                        .rangeEnd(cardRange.getRangeEnd())
                        .build())
                .threeDsMethodUrl(cardRange.getThreeDsMethodUrl())
                .build();
    }
}

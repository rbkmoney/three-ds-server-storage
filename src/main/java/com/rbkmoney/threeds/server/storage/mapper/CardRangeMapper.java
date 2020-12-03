package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.damsel.three_ds_server_storage.ThreeDsSecondVersion;
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
                .acsStartProtocolVersion(cardRange.getAcsStart())
                .acsEndProtocolVersion(cardRange.getAcsEnd())
                .dsStartProtocolVersion(cardRange.getDsStart())
                .dsEndProtocolVersion(cardRange.getDsEnd())
                .acsInformationIndicator(cardRange.getAcsInformationIndicator())
                .threeDsMethodUrl(cardRange.getThreeDsMethodUrl())
                .build();
    }

    public ThreeDsSecondVersion fromEntityToThreeDsSecondVersion(CardRangeEntity cardRangeEntity) {
        return new ThreeDsSecondVersion()
                .setProviderId(cardRangeEntity.getPk().getProviderId())
                .setAcsStart(cardRangeEntity.getAcsStartProtocolVersion())
                .setAcsEnd(cardRangeEntity.getAcsEndProtocolVersion())
                .setDsStart(cardRangeEntity.getDsStartProtocolVersion())
                .setDsEnd(cardRangeEntity.getDsEndProtocolVersion())
                .setThreeDsMethodUrl(cardRangeEntity.getThreeDsMethodUrl());
    }
}

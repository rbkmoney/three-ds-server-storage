package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.threeds.server.domain.rbkmoney.cardrange.CardRange;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class CardRangeMapper {

    public List<CardRangeEntity> fromJsonToEntity(List<CardRange> cardRanges, String providerId) {
        return Optional.of(cardRanges)
                .orElse(emptyList())
                .stream()
                .map(dto -> fromJsonToEntity(dto, providerId))
                .collect(toList());
    }

    private CardRangeEntity fromJsonToEntity(CardRange cardRange, String providerId) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(Long.parseLong(cardRange.getStartRange()))
                        .rangeEnd(Long.parseLong(cardRange.getEndRange()))
                        .build())
                .build();
    }

    public com.rbkmoney.damsel.three_ds_server_storage.CardRange fromEntityToThrift(CardRangeEntity entity) {
        return new com.rbkmoney.damsel.three_ds_server_storage.CardRange()
                .setRangeStart(entity.getPk().getRangeStart())
                .setRangeEnd(entity.getPk().getRangeEnd());
    }
}

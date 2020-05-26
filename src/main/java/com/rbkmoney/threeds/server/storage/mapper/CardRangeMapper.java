package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.threeds.server.dto.CardRangeDTO;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import org.springframework.stereotype.Service;

@Service
public class CardRangeMapper {

    public CardRangeEntity toEntity(CardRangeDTO dto, String providerId) {
        return CardRangeEntity.builder()
                .providerId(providerId)
                .rangeStart(Long.parseLong(dto.getStartRange()))
                .rangeEnd(Long.parseLong(dto.getEndRange()))
                .build();
    }

    public CardRange toDomain(CardRangeEntity entity) {
        return new CardRange()
                .setRangeStart(entity.getRangeStart())
                .setRangeEnd(entity.getRangeEnd());
    }
}

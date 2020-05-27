package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.damsel.three_ds_server_storage.CardRange;
import com.rbkmoney.threeds.server.dto.CardRangeDTO;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class CardRangeMapper {

    public List<CardRangeEntity> toEntities(List<CardRangeDTO> dtos, String providerId) {
        return Optional.of(dtos)
                .orElse(emptyList())
                .stream()
                .map(dto -> toEntity(dto, providerId))
                .collect(toList());
    }

    private CardRangeEntity toEntity(CardRangeDTO dto, String providerId) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(Long.parseLong(dto.getStartRange()))
                        .rangeEnd(Long.parseLong(dto.getEndRange()))
                        .build())
                .build();
    }

    public CardRange toDomain(CardRangeEntity entity) {
        return new CardRange()
                .setRangeStart(entity.getPk().getRangeStart())
                .setRangeEnd(entity.getPk().getRangeEnd());
    }
}

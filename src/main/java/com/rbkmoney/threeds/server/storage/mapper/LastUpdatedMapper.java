package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LastUpdatedMapper {

    public LastUpdatedEntity toEntity(String providerId) {
        return LastUpdatedEntity.builder()
                .providerId(providerId)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
}

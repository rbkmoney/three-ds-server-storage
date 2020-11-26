package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.threeds.server.storage.entity.SerialNumberEntity;
import org.springframework.stereotype.Service;

@Service
public class SerialNumberMapper {

    public SerialNumberEntity toEntity(String serialNumber, String providerId) {
        return SerialNumberEntity.builder()
                .providerId(providerId)
                .serialNumber(serialNumber)
                .build();
    }
}

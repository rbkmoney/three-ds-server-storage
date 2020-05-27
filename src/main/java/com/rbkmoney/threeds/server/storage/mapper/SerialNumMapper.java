package com.rbkmoney.threeds.server.storage.mapper;

import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import org.springframework.stereotype.Service;

@Service
public class SerialNumMapper {

    public SerialNumEntity toEntity(String serialNum, String providerId) {
        return SerialNumEntity.builder()
                .providerId(providerId)
                .serialNum(serialNum)
                .build();
    }
}

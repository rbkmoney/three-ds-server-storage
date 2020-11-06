package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.mapper.SerialNumMapper;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SerialNumService {

    private final SerialNumRepository serialNumRepository;
    private final SerialNumMapper serialNumMapper;

    public String getCurrentSerialNum(String providerId) {
        return serialNumRepository.findByProviderId(providerId)
                .map(SerialNumEntity::getSerialNum)
                .orElse(null);
    }

    @Transactional
    public void save(String providerId, String serialNum) {
        SerialNumEntity serialNumEntity = serialNumMapper.toEntity(serialNum, providerId);
        serialNumRepository.save(serialNumEntity);
    }
}

package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.mapper.SerialNumMapper;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SerialNumService {

    private final SerialNumRepository serialNumRepository;
    private final SerialNumMapper serialNumMapper;

    @Transactional
    public void save(String providerId, String serialNum) {
        log.debug("Trying to save SerialNum, providerId={}", providerId);

        SerialNumEntity serialNumEntity = serialNumMapper.toEntity(serialNum, providerId);

        serialNumRepository.save(serialNumEntity);
    }

    public Optional<String> get(String providerId) {
        log.debug("Trying to get SerialNum, providerId={}", providerId);

        return serialNumRepository.findByProviderId(providerId)
                .map(SerialNumEntity::getSerialNum);
    }
}

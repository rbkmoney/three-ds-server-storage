package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.storage.entity.SerialNumberEntity;
import com.rbkmoney.threeds.server.storage.mapper.SerialNumberMapper;
import com.rbkmoney.threeds.server.storage.repository.SerialNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SerialNumberService {

    private final SerialNumberRepository serialNumberRepository;
    private final SerialNumberMapper serialNumberMapper;

    public void save(String providerId, String serialNumber) {
        log.info("Trying to save SerialNumberEntity, providerId={}", providerId);

        SerialNumberEntity entity = serialNumberMapper.toEntity(serialNumber, providerId);

        serialNumberRepository.save(entity);
    }

    public Optional<String> get(String providerId) {
        log.info("Trying to get SerialNumberEntity, providerId={}", providerId);

        return serialNumberRepository.findById(providerId)
                .map(SerialNumberEntity::getSerialNumber);
    }

    public void delete(String providerId) {
        log.info("Trying to delete SerialNumberEntity, providerId={}", providerId);

        if (serialNumberRepository.existsById(providerId)) {
            serialNumberRepository.deleteById(providerId);
        }
    }
}

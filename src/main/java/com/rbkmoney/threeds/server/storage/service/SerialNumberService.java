package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.storage.entity.SerialNumberEntity;
import com.rbkmoney.threeds.server.storage.mapper.SerialNumberMapper;
import com.rbkmoney.threeds.server.storage.repository.SerialNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SerialNumberService {

    private final SerialNumberRepository serialNumberRepository;
    private final SerialNumberMapper serialNumberMapper;

    @Transactional
    public void save(String providerId, String serialNumber) {
        log.debug("Trying to save SerialNumberEntity, providerId={}", providerId);

        SerialNumberEntity entity = serialNumberMapper.toEntity(serialNumber, providerId);

        serialNumberRepository.save(entity);
    }

    public Optional<String> get(String providerId) {
        log.debug("Trying to get SerialNumberEntity, providerId={}", providerId);

        return serialNumberRepository.findByProviderId(providerId)
                .map(SerialNumberEntity::getSerialNumber);
    }

    @Transactional
    public void delete(String providerId) {
        log.info("Trying to delete SerialNumberEntity, providerId={}", providerId);

        serialNumberRepository.deleteById(providerId);
    }
}

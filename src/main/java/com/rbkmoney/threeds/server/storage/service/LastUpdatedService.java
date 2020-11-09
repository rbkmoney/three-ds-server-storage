package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import com.rbkmoney.threeds.server.storage.mapper.LastUpdatedMapper;
import com.rbkmoney.threeds.server.storage.repository.LastUpdatedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastUpdatedService {

    private final LastUpdatedRepository lastUpdatedRepository;
    private final LastUpdatedMapper lastUpdatedMapper;

    @Transactional
    public void save(String providerId) {
        log.debug("Trying to save LastUpdated, providerId={}", providerId);

        LastUpdatedEntity lastUpdatedEntity = lastUpdatedMapper.toEntity(providerId);

        lastUpdatedRepository.save(lastUpdatedEntity);
    }
}

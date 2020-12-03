package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.SerialNumberEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class SerialNumberRepositoryTest extends AbstractDaoConfig {

    @Autowired
    private SerialNumberRepository repository;

    @Before
    public void setUp() {
        SerialNumberEntity trap = SerialNumberEntity.builder()
                .providerId("TRAP")
                .serialNumber("trap")
                .build();

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetSerialNumber() {
        // Given
        SerialNumberEntity entity = SerialNumberEntity.builder()
                .providerId("1")
                .serialNumber("serial")
                .build();

        // When
        repository.save(entity);
        Optional<SerialNumberEntity> saved = repository.findById("1");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(entity);
    }

    @Test
    public void shouldKeepOnlyTheNewestSerialNumber() {
        // Given
        SerialNumberEntity first = SerialNumberEntity.builder()
                .providerId("2")
                .serialNumber("1")
                .build();

        SerialNumberEntity second = SerialNumberEntity.builder()
                .providerId("2")
                .serialNumber("2")
                .build();

        SerialNumberEntity third = SerialNumberEntity.builder()
                .providerId("2")
                .serialNumber("3")
                .build();

        // When
        repository.save(first);
        repository.save(second);
        repository.save(third);
        Optional<SerialNumberEntity> saved = repository.findById("2");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(third);
    }

    @Test
    public void shouldDeleteSerialNumberByProviderId() {
        // Given
        SerialNumberEntity first = SerialNumberEntity.builder()
                .providerId("2")
                .serialNumber("1")
                .build();

        SerialNumberEntity second = SerialNumberEntity.builder()
                .providerId("2")
                .serialNumber("2")
                .build();

        SerialNumberEntity third = SerialNumberEntity.builder()
                .providerId("3")
                .serialNumber("3")
                .build();

        // When
        repository.save(first);
        repository.save(second);
        repository.save(third);

        // When
        repository.deleteById("3");

        Optional<SerialNumberEntity> saved = repository.findById("3");

        // Then
        assertThat(saved).isEmpty();

        saved = repository.findById("2");

        // Then
        assertThat(saved).isNotEmpty();
    }
}

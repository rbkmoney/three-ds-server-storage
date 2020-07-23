package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SerialNumRepositoryTest extends PostgresRepositoryTest {

    @Autowired
    private SerialNumRepository repository;

    @Before
    public void setUp() {
        SerialNumEntity trap = SerialNumEntity.builder()
                .providerId("TRAP")
                .serialNum("trap")
                .build();

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetSerialNum() {
        // Given
        SerialNumEntity entity = SerialNumEntity.builder()
                .providerId("1")
                .serialNum("serial")
                .build();

        // When
        repository.save(entity);
        Optional<SerialNumEntity> saved = repository.findByProviderId("1");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(entity);
    }

    @Test
    public void shouldKeepOnlyTheNewestSerialNum() {
        // Given
        SerialNumEntity first = SerialNumEntity.builder()
                .providerId("2")
                .serialNum("1")
                .build();

        SerialNumEntity second = SerialNumEntity.builder()
                .providerId("2")
                .serialNum("2")
                .build();

        SerialNumEntity third = SerialNumEntity.builder()
                .providerId("2")
                .serialNum("3")
                .build();

        // When
        repository.save(first);
        repository.save(second);
        repository.save(third);
        Optional<SerialNumEntity> saved = repository.findByProviderId("2");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(third);
    }
}

package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import com.rbkmoney.threeds.server.storage.mapper.LastUpdatedMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LastUpdatedRepositoryTest extends AbstractDaoConfig {

    @Autowired
    private LastUpdatedRepository repository;

    @Autowired
    private LastUpdatedMapper mapper;

    @Before
    public void setUp() {
        LastUpdatedEntity trap = mapper.toEntity("trap");

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetTest() {
        LastUpdatedEntity entity = mapper.toEntity("visa");

        repository.save(entity);

        Optional<LastUpdatedEntity> saved = repository.findById("visa");

        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(entity);
    }

    @Test
    public void shouldKeepOnlyTheNewestTest() throws Exception {
        repository.save(mapper.toEntity("visa"));
        Optional<LastUpdatedEntity> firstSaved = repository.findById("visa");

        assertTrue(firstSaved.isPresent());

        TimeUnit.MILLISECONDS.sleep(200);

        repository.save(mapper.toEntity("visa"));

        LastUpdatedEntity third = mapper.toEntity("visa");
        repository.save(third);
        Optional<LastUpdatedEntity> lastSaved = repository.findById("visa");

        assertTrue(lastSaved.isPresent());
        assertThat(lastSaved.get()).isEqualTo(third);
        assertThat(firstSaved.get().getLastUpdatedAt()).isBefore(lastSaved.get().getLastUpdatedAt());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    public void shouldDeleteLastUpdatedByProviderId() {
        repository.save(mapper.toEntity("1"));
        repository.save(mapper.toEntity("2"));

        // When
        repository.deleteById("2");

        Optional<LastUpdatedEntity> saved = repository.findById("2");

        // Then
        assertThat(saved).isEmpty();

        saved = repository.findById("1");

        assertThat(saved).isNotEmpty();
    }
}

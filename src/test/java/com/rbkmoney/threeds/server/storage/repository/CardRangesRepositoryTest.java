package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class CardRangesRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private CardRangesRepository repository;

    @Before
    public void setUp() {
        CardRangeEntity trap = CardRangeEntity.builder()
                .providerId("TRAP")
                .rangeEnd(1L)
                .rangeEnd(9L)
                .build();

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetCardRanges() {
        // Given
        CardRangeEntity first = CardRangeEntity.builder()
                .providerId(TEST_PROVIDER)
                .rangeStart(0L)
                .rangeEnd(1000L)
                .build();

        CardRangeEntity second = CardRangeEntity.builder()
                .providerId(TEST_PROVIDER)
                .rangeStart(1001L)
                .rangeEnd(2000L)
                .build();

        // When
        repository.saveAll(List.of(first, second));
        List<CardRangeEntity> saved = repository.findByProviderId(TEST_PROVIDER);

        // Then
        assertThat(saved).containsExactlyInAnyOrder(first, second);
    }

    @Test
    public void shouldReturnEmptyListIfNoCardRangesFound() {
        // Given

        // When
        List<CardRangeEntity> saved = repository.findByProviderId(TEST_PROVIDER);

        // Then
        assertThat(saved).isEmpty();
    }
}

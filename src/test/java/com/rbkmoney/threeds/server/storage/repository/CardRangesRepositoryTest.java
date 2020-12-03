package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CardRangesRepositoryTest extends AbstractDaoConfig {

    @Autowired
    private CardRangeRepository repository;

    @Before
    public void setUp() {
        CardRangeEntity trap = entity("TRAP", 1L, 9L);

        repository.deleteAll();
        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetCardRanges() {
        // Given
        CardRangeEntity first = entity("1", 0L, 1000L);
        CardRangeEntity second = entity("1", 1001L, 2000L);

        // When
        repository.saveAll(List.of(first, second));
        List<CardRangeEntity> saved = repository.findByPkProviderId("1");

        // Then
        assertThat(saved).containsExactlyInAnyOrder(first, second);
    }

    @Test
    public void shouldReturnEmptyListIfNoCardRangesFound() {
        // Given
        // -

        // When
        List<CardRangeEntity> saved = repository.findByPkProviderId("2");

        // Then
        assertThat(saved).isEmpty();
    }

    @Test
    public void shouldDeleteCardRanges() {
        // Given
        CardRangeEntity first = entity("3", 0L, 1000L);
        CardRangeEntity second = entity("3", 1001L, 2000L);

        repository.saveAll(List.of(first, second));

        // When
        repository.deleteAll(List.of(first, second));
        List<CardRangeEntity> saved = repository.findByPkProviderId("3");

        // Then
        assertThat(saved).isEmpty();
    }

    @Test
    public void shouldNotInsertDuplicates() {
        // Given
        CardRangeEntity first = entity("4", 0L, 1000L);
        CardRangeEntity duplicate = entity("4", 0L, 1000L);

        // When
        repository.saveAll(List.of(first, duplicate));
        List<CardRangeEntity> saved = repository.findByPkProviderId("4");

        // Then
        assertThat(saved).hasSize(1);
    }

    @Test
    public void shouldInsert1000CardRanges() {
        // Given
        List<CardRangeEntity> cardRanges = new ArrayList<>();

        for (long i = 0; i < 1000L; i++) {
            CardRangeEntity range = entity("5", i, i + 1);

            cardRanges.add(range);
        }

        // When
        repository.saveAll(cardRanges);
        List<CardRangeEntity> saved = repository.findByPkProviderId("5");

        // Then
        assertThat(saved).hasSize(1000);
    }

    @Test
    public void shouldDeleteCardRangesByProviderId() {
        // Given
        CardRangeEntity first = entity("3", 0L, 1000L);
        CardRangeEntity second = entity("3", 1001L, 2000L);
        CardRangeEntity third = entity("4", 1001L, 2000L);

        repository.saveAll(List.of(first, second, third));

        // When
        repository.deleteAllByPkProviderId("3");
        List<CardRangeEntity> saved = repository.findByPkProviderId("3");

        // Then
        assertThat(saved).isEmpty();

        saved = repository.findByPkProviderId("4");

        assertThat(saved).isNotEmpty();
    }

    private CardRangeEntity entity(String providerId, long rangeStart, long rangeEnd) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .build())
                .acsStartProtocolVersion("2.1.0")
                .acsEndProtocolVersion("2.1.0")
                .dsStartProtocolVersion("2.1.0")
                .dsEndProtocolVersion("2.1.0")
                .acsInformationIndicator("2.1.0")
                .threeDsMethodUrl("url")
                .build();
    }
}

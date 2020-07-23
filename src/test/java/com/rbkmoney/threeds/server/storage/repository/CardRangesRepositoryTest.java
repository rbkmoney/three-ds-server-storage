package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class CardRangesRepositoryTest extends AbstractDaoConfig {

    @Autowired
    private CardRangeRepository repository;

    @Before
    public void setUp() {
        CardRangeEntity trap = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("TRAP")
                        .rangeStart(1L)
                        .rangeEnd(9L)
                        .build())
                .build();

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetCardRanges() {
        // Given
        CardRangeEntity first = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("1")
                        .rangeStart(0L)
                        .rangeEnd(1000L)
                        .build())
                .build();

        CardRangeEntity second = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("1")
                        .rangeStart(1001L)
                        .rangeEnd(2000L)
                        .build())
                .build();

        // When
        repository.saveAll(List.of(first, second));
        List<CardRangeEntity> saved = repository.findByPkProviderId("1");

        // Then
        assertThat(saved).containsExactlyInAnyOrder(first, second);
    }

    @Test
    public void shouldReturnEmptyListIfNoCardRangesFound() {
        // Given

        // When
        List<CardRangeEntity> saved = repository.findByPkProviderId("2");

        // Then
        assertThat(saved).isEmpty();
    }

    @Test
    public void shouldDeleteCardRanges() {
        // Given
        CardRangeEntity first = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("3")
                        .rangeStart(0L)
                        .rangeEnd(1000L)
                        .build())
                .build();

        CardRangeEntity second = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("3")
                        .rangeStart(1001L)
                        .rangeEnd(2000L)
                        .build())
                .build();

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
        CardRangeEntity first = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("4")
                        .rangeStart(0L)
                        .rangeEnd(1000L)
                        .build())
                .build();

        CardRangeEntity duplicate = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId("4")
                        .rangeStart(0L)
                        .rangeEnd(1000L)
                        .build())
                .build();

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
            CardRangeEntity range = CardRangeEntity.builder()
                    .pk(CardRangePk.builder()
                            .providerId("5")
                            .rangeStart(i)
                            .rangeEnd(i + 1)
                            .build())
                    .build();

            cardRanges.add(range);
        }

        // When
        repository.saveAll(cardRanges);
        List<CardRangeEntity> saved = repository.findByPkProviderId("5");

        // Then
        assertThat(saved).hasSize(1000);
    }
}

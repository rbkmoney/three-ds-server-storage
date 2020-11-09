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
    private CardRangeRepository cardRangeRepository;

    @Before
    public void setUp() {
        CardRangeEntity trap = entity("TRAP", 1L, 9L);

        cardRangeRepository.deleteAll();
        cardRangeRepository.save(trap);
    }

    @Test
    public void shouldSaveAndGetCardRanges() {
        // Given
        CardRangeEntity first = entity("1", 0L, 1000L);
        CardRangeEntity second = entity("1", 1001L, 2000L);

        // When
        cardRangeRepository.saveAll(List.of(first, second));
        List<CardRangeEntity> saved = cardRangeRepository.findByPkProviderId("1");

        // Then
        assertThat(saved).containsExactlyInAnyOrder(first, second);
    }

    @Test
    public void shouldReturnEmptyListIfNoCardRangesFound() {
        // Given
        // -

        // When
        List<CardRangeEntity> saved = cardRangeRepository.findByPkProviderId("2");

        // Then
        assertThat(saved).isEmpty();
    }

    @Test
    public void shouldDeleteCardRanges() {
        // Given
        CardRangeEntity first = entity("3", 0L, 1000L);
        CardRangeEntity second = entity("3", 1001L, 2000L);

        cardRangeRepository.saveAll(List.of(first, second));

        // When
        cardRangeRepository.deleteAll(List.of(first, second));
        List<CardRangeEntity> saved = cardRangeRepository.findByPkProviderId("3");

        // Then
        assertThat(saved).isEmpty();
    }

    @Test
    public void shouldNotInsertDuplicates() {
        // Given
        CardRangeEntity first = entity("4", 0L, 1000L);
        CardRangeEntity duplicate = entity("4", 0L, 1000L);

        // When
        cardRangeRepository.saveAll(List.of(first, duplicate));
        List<CardRangeEntity> saved = cardRangeRepository.findByPkProviderId("4");

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
        cardRangeRepository.saveAll(cardRanges);
        List<CardRangeEntity> saved = cardRangeRepository.findByPkProviderId("5");

        // Then
        assertThat(saved).hasSize(1000);
    }

    private CardRangeEntity entity(String providerId, long rangeStart, long rangeEnd) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .build())
                .build();
    }
}

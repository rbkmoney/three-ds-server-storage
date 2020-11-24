package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.cardrange.ActionInd;
import com.rbkmoney.threeds.server.domain.rbkmoney.cardrange.RBKMoneyCardRange;
import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class CardRangeServiceTest extends AbstractDaoConfig {

    @Autowired
    private CardRangeRepository cardRangeRepository;

    @Autowired
    private CardRangeService cardRangeService;

    @Before
    public void setUp() {
        CardRangeEntity trap = entity("TRAP", 1L, 9L);

        cardRangeRepository.deleteAll();
        cardRangeRepository.save(trap);
    }

    @Test
    public void doesNotExistsCardRangesTest() throws Exception {
        // Given
        CardRangeEntity first = entity("1", 1001L, 2000L);
        CardRangeEntity second = entity("1", 3001L, 3003L);

        // When
        cardRangeRepository.saveAll(List.of(first, second));

        assertFalse(cardRangeService.doesNotExistsCardRanges("1"));
        assertTrue(cardRangeService.doesNotExistsCardRanges("2"));
    }

    @Test
    public void existsFreeSpaceForNewCardRangeTest() throws Exception {
        // Given
        CardRangeEntity first = entity("1", 1001L, 2000L);
        CardRangeEntity second = entity("1", 3001L, 3003L);

        // When
        cardRangeRepository.saveAll(List.of(first, second));

        // free space
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 2001L, 2002L));
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 2001L, 3000L));
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3004L, 5000L));
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 0L, 500L));
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 0L, 1000L));
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3001L, 3003L));
        assertTrue(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 1001L, 2000L));

        // crossing with start range
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 2001L, 3001L));
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 2001L, 3002L));
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3001L, 3001L));

        // crossing with end range
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3003L, 3005L));
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3002L, 3005L));
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3003L, 3003L));

        // covering full card range
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3000L, 3005L));

        // in card range
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 3002L, 3002L));
        assertFalse(cardRangeRepository.existsFreeSpaceForNewCardRange("1", 1002L, 1999L));
    }

    @Test
    public void existsCardRangeTest() throws Exception {
        // Given
        CardRangeEntity first = entity("1", 1001L, 2000L);
        CardRangeEntity second = entity("1", 3001L, 3003L);

        // When
        cardRangeRepository.saveAll(List.of(first, second));

        // positive
        assertTrue(cardRangeService.existsCardRange("1", 1001L, 2000L));
        assertTrue(cardRangeService.existsCardRange("1", 3001L, 3003L));

        // negative
        assertFalse(cardRangeService.existsCardRange("1", 3002L, 3003L));
        assertFalse(cardRangeService.existsCardRange("1", 3001L, 3002L));
        assertFalse(cardRangeService.existsCardRange("1", 1001L, 1001L));
        assertFalse(cardRangeService.existsCardRange("1", 1002L, 1003L));
        assertFalse(cardRangeService.existsCardRange("1", 2000L, 2000L));
        assertFalse(cardRangeService.existsCardRange("1", 0L, 10000L));
    }

    @Test
    public void saveDeleteAllTest() throws Exception {
        List<RBKMoneyCardRange> rbkMoneyCardRanges = new ArrayList<>(List.of(
                new RBKMoneyCardRange("5", "6", ActionInd.ADD_CARD_RANGE_TO_CACHE, "url"),
                new RBKMoneyCardRange("7", "8", ActionInd.ADD_CARD_RANGE_TO_CACHE, "url")
        ));

        cardRangeService.saveAll("1", rbkMoneyCardRanges);

        assertEquals(0, rbkMoneyCardRanges.size());
        assertEquals(2, cardRangeRepository.findByPkProviderId("1").size());

        rbkMoneyCardRanges = new ArrayList<>(List.of(
                new RBKMoneyCardRange("1", "2", ActionInd.ADD_CARD_RANGE_TO_CACHE, "url"),
                new RBKMoneyCardRange("3", "4", ActionInd.ADD_CARD_RANGE_TO_CACHE, "url"),
                new RBKMoneyCardRange("5", "6", ActionInd.MODIFY_CARD_RANGE_DATA, "url"),
                new RBKMoneyCardRange("7", "8", ActionInd.MODIFY_CARD_RANGE_DATA, "url"),
                new RBKMoneyCardRange("1", "2", ActionInd.DELETE_CARD_RANGE_FROM_CACHE, "url"),
                new RBKMoneyCardRange("7", "8", ActionInd.DELETE_CARD_RANGE_FROM_CACHE, "url")
        ));

        cardRangeService.saveAll("1", rbkMoneyCardRanges);

        assertEquals(2, rbkMoneyCardRanges.size());
        assertEquals(4, cardRangeRepository.findByPkProviderId("1").size());

        cardRangeService.deleteAll("1", rbkMoneyCardRanges);

        assertEquals(0, rbkMoneyCardRanges.size());
        assertEquals(2, cardRangeRepository.findByPkProviderId("1").size());
    }

    @Test
    public void getProviderIdTest() throws Exception {
        // Given
        CardRangeEntity first = entity("1", 1001L, 2000L);
        CardRangeEntity second = entity("1", 3001L, 3003L);
        CardRangeEntity third = entity("2", 4000L, 4001L);

        // When
        cardRangeRepository.saveAll(List.of(first, second, third));

        assertEquals("1", cardRangeService.getProviderId(1001L).get());
        assertEquals("1", cardRangeService.getProviderId(1002L).get());
        assertEquals("1", cardRangeService.getProviderId(1999L).get());
        assertEquals("1", cardRangeService.getProviderId(2000L).get());
        assertEquals("1", cardRangeService.getProviderId(3002L).get());
        assertEquals("2", cardRangeService.getProviderId(4000L).get());
        assertTrue(cardRangeService.getProviderId(500L).isEmpty());
        assertTrue(cardRangeService.getProviderId(5000L).isEmpty());
    }

    private CardRangeEntity entity(String providerId, long rangeStart, long rangeEnd) {
        return CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .build())
                .threeDsMethodUrl("url")
                .build();
    }
}

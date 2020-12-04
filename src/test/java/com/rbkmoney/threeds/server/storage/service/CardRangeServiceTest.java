package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.damsel.threeds.server.storage.*;
import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        assertTrue(cardRangeService.existsCardRange("1", addCardRange(1001L, 2000L)));
        assertTrue(cardRangeService.existsCardRange("1", addCardRange(3001L, 3003L)));

        // negative
        assertFalse(cardRangeService.existsCardRange("1", addCardRange(3002L, 3003L)));
        assertFalse(cardRangeService.existsCardRange("1", addCardRange(3001L, 3002L)));
        assertFalse(cardRangeService.existsCardRange("1", addCardRange(1001L, 1001L)));
        assertFalse(cardRangeService.existsCardRange("1", addCardRange(1002L, 1003L)));
        assertFalse(cardRangeService.existsCardRange("1", addCardRange(2000L, 2000L)));
        assertFalse(cardRangeService.existsCardRange("1", addCardRange(0L, 10000L)));
    }

    @Test
    public void saveDeleteAllTest() throws Exception {
        List<CardRange> rbkMoneyCardRanges = new ArrayList<>(List.of(
                addCardRange(5, 6),
                addCardRange(7, 8)
        ));

        cardRangeService.saveCardRangeEntities("1", rbkMoneyCardRanges);

        assertEquals(0, rbkMoneyCardRanges.size());
        assertEquals(2, cardRangeRepository.findByPkProviderId("1").size());

        rbkMoneyCardRanges = new ArrayList<>(List.of(
                addCardRange(1, 2),
                addCardRange(3, 4),
                modifyCardRange(5, 6),
                modifyCardRange(7, 8),
                deleteCardRange(1, 2),
                deleteCardRange(7, 8)
        ));

        cardRangeService.saveCardRangeEntities("1", rbkMoneyCardRanges);

        assertEquals(2, rbkMoneyCardRanges.size());
        assertEquals(4, cardRangeRepository.findByPkProviderId("1").size());

        cardRangeService.deleteCardRangeEntities("1", rbkMoneyCardRanges);

        assertEquals(0, rbkMoneyCardRanges.size());
        assertEquals(2, cardRangeRepository.findByPkProviderId("1").size());
    }

    @Test
    public void getProviderIdTest() throws Exception {
        // Given
        CardRangeEntity first = entity("1", 12345671001L, 12345672000L);
        CardRangeEntity second = entity("1", 12345673001L, 12345673003L);
        CardRangeEntity third = entity("2", 12345674000L, 12345674001L);

        // When
        cardRangeRepository.saveAll(List.of(first, second, third));

        assertEquals("1", cardRangeService.getProviderId(12345671001L).get());
        assertEquals("1", cardRangeService.getProviderId(12345671002L).get());
        assertEquals("1", cardRangeService.getProviderId(12345671999L).get());
        assertEquals("1", cardRangeService.getProviderId(12345672000L).get());
        assertEquals("1", cardRangeService.getProviderId(12345673002L).get());
        assertEquals("2", cardRangeService.getProviderId(12345674000L).get());
        assertTrue(cardRangeService.getProviderId(1234567500L).isEmpty());
        assertTrue(cardRangeService.getProviderId(12345675000L).isEmpty());
    }

    @Test
    public void getAccountNumberVersionTest() throws Exception {
        // Given
        CardRangeEntity first = entity("1", 12345671001L, 12345672000L);
        CardRangeEntity second = entity("1", 12345673001L, 12345673003L);
        CardRangeEntity third = entity("2", 12345674000L, 12345674001L);

        // When
        cardRangeRepository.saveAll(List.of(first, second, third));

        assertEquals("1", cardRangeService.getThreeDsSecondVersion(12345671001L).get().getProviderId());
        assertEquals("1", cardRangeService.getThreeDsSecondVersion(12345671002L).get().getProviderId());
        assertEquals("1", cardRangeService.getThreeDsSecondVersion(12345671999L).get().getProviderId());
        assertEquals("1", cardRangeService.getThreeDsSecondVersion(12345672000L).get().getProviderId());
        assertEquals("1", cardRangeService.getThreeDsSecondVersion(12345673002L).get().getProviderId());
        assertEquals("2", cardRangeService.getThreeDsSecondVersion(12345674000L).get().getProviderId());
        assertTrue(cardRangeService.getThreeDsSecondVersion(1234567500L).isEmpty());
        assertTrue(cardRangeService.getThreeDsSecondVersion(12345675000L).isEmpty());
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

    private CardRange addCardRange(long rangeStart, long rangeEnd) {
        return new CardRange(rangeStart, rangeEnd, add(), "2.1.0", "2.1.0", "2.1.0", "2.1.0");
    }

    private CardRange modifyCardRange(long rangeStart, long rangeEnd) {
        return new CardRange(rangeStart, rangeEnd, modify(), "2.1.0", "2.1.0", "2.1.0", "2.1.0");
    }

    private CardRange deleteCardRange(long rangeStart, long rangeEnd) {
        return new CardRange(rangeStart, rangeEnd, delete(), "2.1.0", "2.1.0", "2.1.0", "2.1.0");
    }

    private Action add() {
        return Action.add_card_range(new Add());
    }

    private Action modify() {
        return Action.modify_card_range(new Modify());
    }

    private Action delete() {
        return Action.delete_card_range(new Delete());
    }
}

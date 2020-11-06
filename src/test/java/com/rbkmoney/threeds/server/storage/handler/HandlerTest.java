package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.threeds.server.storage.config.AbstractConfigWithoutDao;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class HandlerTest extends AbstractConfigWithoutDao {

    private static final int TIMEOUT = 555000;
    private static final String PROVIDER_ID = "visa";

    @Value("${local.server.port}")
    private int port;

    private CardRangesStorageSrv.Iface cardRangesStorageClient;
    private ChallengeFlowTransactionInfoStorageSrv.Iface challengeFlowTransactionInfoStorageClient;

    @Before
    public void setUp() throws Exception {
        cardRangesStorageClient = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:" + port + "/three-ds-server-storage/card-ranges"))
                .withNetworkTimeout(TIMEOUT)
                .build(CardRangesStorageSrv.Iface.class);
        challengeFlowTransactionInfoStorageClient = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:" + port + "/three-ds-server-storage/challenge-flow-transaction-info"))
                .withNetworkTimeout(TIMEOUT)
                .build(ChallengeFlowTransactionInfoStorageSrv.Iface.class);
    }

    @Test
    public void shouldReturnTrueForEmptyStorage() throws Exception {
        when(cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(PROVIDER_ID)).thenReturn(false);

        boolean expected = cardRangesStorageClient.isStorageEmpty(PROVIDER_ID);

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForNotEmptyStorage() throws Exception {
        when(cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(PROVIDER_ID)).thenReturn(true);

        boolean expected = cardRangesStorageClient.isStorageEmpty(PROVIDER_ID);

        assertFalse(expected);
    }

    @Test
    public void shouldReturnTrueForValidAddCardRange() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(new CardRange(1, 2, add())));

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidAddCardRange() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(new CardRange(1, 2, add())));

        assertFalse(expected);
    }

    @Test
    public void shouldReturnTrueForValidModifyCardRange() throws Exception {
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(new CardRange(1, 2, modify())));

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidModifyCardRange() throws Exception {
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(new CardRange(1, 2, modify())));

        assertFalse(expected);
    }

    @Test
    public void shouldReturnTrueForValidCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidAddInCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(false);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertFalse(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidModifyInCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(3, 4)))).thenReturn(false);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(5, 6)))).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertFalse(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidDeleteInCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(3, 4)))).thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(5, 6)))).thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertFalse(expected);
    }

    @Test
    public void shouldReturnTrueForAcctNumberInRange() throws Exception {
        when(cardRangeRepository.existsCardRangeEntityByPkProviderIdIsAndPkRangeStartLessThanEqualAndPkRangeEndGreaterThanEqual(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isInCardRange(PROVIDER_ID, 1L);

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForAcctNumberOutRange() throws Exception {
        when(cardRangeRepository.existsCardRangeEntityByPkProviderIdIsAndPkRangeStartLessThanEqualAndPkRangeEndGreaterThanEqual(eq(PROVIDER_ID), anyLong(), anyLong())).thenReturn(false);

        boolean expected = cardRangesStorageClient.isInCardRange(PROVIDER_ID, 1L);

        assertFalse(expected);
    }

    @Test(expected = ChallengeFlowTransactionInfoNotFound.class)
    public void shouldThrowWhenNullTransactionTest() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        when(challengeFlowTransactionInfoRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        challengeFlowTransactionInfoStorageClient.getChallengeFlowTransactionInfo(transactionId);
    }

    @Test
    public void shouldHandleTransactionIdRequestTest() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        ChallengeFlowTransactionInfoEntity challengeFlowTransactionInfoEntity = ChallengeFlowTransactionInfoEntity.builder()
                .transactionId(transactionId)
                .deviceChannel("device")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .acsDecConInd("acs")
                .providerId("1")
                .messageVersion("2.1.0")
                .acsUrl("1")
                .build();

        when(challengeFlowTransactionInfoRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(challengeFlowTransactionInfoEntity));

        ChallengeFlowTransactionInfo challengeFlowTransactionInfo = challengeFlowTransactionInfoStorageClient.getChallengeFlowTransactionInfo(transactionId);
        assertEquals(transactionId, challengeFlowTransactionInfo.getTransactionId());
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

    private List<CardRange> cardRanges() {
        return List.of(
                new CardRange(1, 2, add()),
                new CardRange(3, 4, modify()),
                new CardRange(5, 6, delete()));
    }

    private CardRangePk cardRangePk(int rangeStart, int rangeEnd) {
        return CardRangePk.builder()
                .providerId(PROVIDER_ID)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();
    }
}

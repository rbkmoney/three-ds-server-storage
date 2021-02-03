package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.threeds.server.storage.Action;
import com.rbkmoney.damsel.threeds.server.storage.Add;
import com.rbkmoney.damsel.threeds.server.storage.CardRange;
import com.rbkmoney.damsel.threeds.server.storage.CardRangesStorageSrv;
import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfo;
import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfoNotFound;
import com.rbkmoney.damsel.threeds.server.storage.ChallengeFlowTransactionInfoStorageSrv;
import com.rbkmoney.damsel.threeds.server.storage.Delete;
import com.rbkmoney.damsel.threeds.server.storage.DirectoryServerProviderIDNotFound;
import com.rbkmoney.damsel.threeds.server.storage.Modify;
import com.rbkmoney.threeds.server.storage.config.AbstractConfigWithoutDao;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
                .withAddress(new URI("http://localhost:" + port +
                        "/three-ds-server-storage/card-ranges"))
                .withNetworkTimeout(TIMEOUT)
                .build(CardRangesStorageSrv.Iface.class);
        challengeFlowTransactionInfoStorageClient = new THSpawnClientBuilder()
                .withAddress(new URI("http://localhost:" + port +
                        "/three-ds-server-storage/challenge-flow-transaction-info"))
                .withNetworkTimeout(TIMEOUT)
                .build(ChallengeFlowTransactionInfoStorageSrv.Iface.class);

        when(cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(eq(PROVIDER_ID))).thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(false);
    }

    @Test
    public void shouldReturnTrueForEmptyStorage() throws Exception {
        when(cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(eq(PROVIDER_ID))).thenReturn(false);

        boolean expected = cardRangesStorageClient.isStorageEmpty(PROVIDER_ID);

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForNotEmptyStorage() throws Exception {
        when(cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(eq(PROVIDER_ID))).thenReturn(true);

        boolean expected = cardRangesStorageClient.isStorageEmpty(PROVIDER_ID);

        assertFalse(expected);
    }

    @Test
    public void shouldReturnValidCardRangesForEmptyStorage() throws Exception {
        when(cardRangeRepository.existsCardRangeEntitiesByPkProviderIdIs(eq(PROVIDER_ID))).thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(cardRange(add())));

        assertTrue(expected);
    }

    @Test
    public void shouldReturnTrueForValidAddCardRange() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong()))
                .thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(cardRange(add())));

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidAddCardRange() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong()))
                .thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(cardRange(add())));

        assertFalse(expected);
    }

    @Test
    public void shouldReturnTrueForValidModifyCardRange() throws Exception {
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(cardRange(modify())));

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidModifyCardRange() throws Exception {
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(false);

        Action modify = modify();
        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, List.of(cardRange(modify)));

        assertFalse(expected);
    }

    @Test
    public void shouldReturnTrueForValidCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong()))
                .thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(any())).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertTrue(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidAddInCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong()))
                .thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertFalse(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidModifyInCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong()))
                .thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(3, 4)))).thenReturn(false);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(5, 6)))).thenReturn(true);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertFalse(expected);
    }

    @Test
    public void shouldReturnFalseForInvalidDeleteInCardRanges() throws Exception {
        when(cardRangeRepository.existsFreeSpaceForNewCardRange(eq(PROVIDER_ID), anyLong(), anyLong()))
                .thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(3, 4)))).thenReturn(true);
        when(cardRangeRepository.existsCardRangeEntityByPkEquals(eq(cardRangePk(5, 6)))).thenReturn(false);

        boolean expected = cardRangesStorageClient.isValidCardRanges(PROVIDER_ID, cardRanges());

        assertFalse(expected);
    }

    @Test
    public void shouldReturnDsProviderIdForAcctNumberInRange() throws Exception {
        when(cardRangeRepository.getProviderIds(anyLong(), any())).thenReturn(new PageImpl<>(List.of(PROVIDER_ID)));

        String expected = cardRangesStorageClient.getDirectoryServerProviderId(1L);

        assertNotNull(expected);
        assertEquals(PROVIDER_ID, expected);
    }

    @Test(expected = DirectoryServerProviderIDNotFound.class)
    public void shouldThrowNotFoundForAcctNumberOutRange() throws Exception {
        when(cardRangeRepository.getProviderIds(anyLong(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        cardRangesStorageClient.getDirectoryServerProviderId(1L);
    }

    @Test(expected = ChallengeFlowTransactionInfoNotFound.class)
    public void shouldThrowNotFoundWhenNullTransactionTest() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        when(challengeFlowTransactionInfoRepository.findById(transactionId)).thenReturn(Optional.empty());

        challengeFlowTransactionInfoStorageClient.getChallengeFlowTransactionInfo(transactionId);
    }

    @Test
    public void shouldHandleTransactionIdRequestTest() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        ChallengeFlowTransactionInfoEntity challengeFlowTransactionInfoEntity =
                ChallengeFlowTransactionInfoEntity.builder()
                        .transactionId(transactionId)
                        .deviceChannel("device")
                        .decoupledAuthMaxTime(LocalDateTime.now())
                        .acsDecConInd("acs")
                        .providerId("1")
                        .messageVersion("2.1.0")
                        .acsUrl("1")
                        .build();

        when(challengeFlowTransactionInfoRepository.findById(transactionId))
                .thenReturn(Optional.of(challengeFlowTransactionInfoEntity));

        ChallengeFlowTransactionInfo challengeFlowTransactionInfo =
                challengeFlowTransactionInfoStorageClient.getChallengeFlowTransactionInfo(transactionId);
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

    private CardRange cardRange(Action action) {
        return new CardRange(1, 2, action, "2.1.0", "2.1.0", "2.1.0", "2.1.0");
    }

    private List<CardRange> cardRanges() {
        return List.of(
                cardRange(add()),
                new CardRange(3, 4, modify(), "2.1.0", "2.1.0", "2.1.0", "2.1.0"),
                new CardRange(5, 6, delete(), "2.1.0", "2.1.0", "2.1.0", "2.1.0"));
    }

    private CardRangePk cardRangePk(int rangeStart, int rangeEnd) {
        return CardRangePk.builder()
                .providerId(PROVIDER_ID)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();
    }
}

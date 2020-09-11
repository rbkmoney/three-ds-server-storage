package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.threeds.server.storage.config.AbstractConfigWithoutDao;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class HandlerTest extends AbstractConfigWithoutDao {

    private static final int TIMEOUT = 555000;

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

    @Test(expected = CardRangesNotFound.class)
    public void shouldThrowWhenEmptyCardRangesTest() throws Exception {
        String providerId = "visa";

        when(cardRangeRepository.findByPkProviderId(providerId)).thenReturn(Collections.emptyList());

        cardRangesStorageClient.getCardRanges(new GetCardRangesRequest(providerId));
    }

    @Test(expected = CardRangesNotFound.class)
    public void shouldThrowWhenNullLastUpdatedTest() throws Exception {
        String providerId = "visa";

        CardRangeEntity cardRangeEntity = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(1)
                        .rangeEnd(2)
                        .build())
                .build();

        when(cardRangeRepository.findByPkProviderId(providerId)).thenReturn(List.of(cardRangeEntity));
        when(lastUpdatedRepository.findByProviderId(providerId)).thenReturn(Optional.empty());

        cardRangesStorageClient.getCardRanges(new GetCardRangesRequest(providerId));
    }

    @Test
    public void shouldHandleGetCardRangesRequestTest() throws Exception {
        String providerId = "visa";
        int rangeStart = 1;
        int rangeEnd = 2;
        LocalDateTime lastUpdatedAt = LocalDateTime.now();

        CardRangeEntity cardRangeEntity = CardRangeEntity.builder()
                .pk(CardRangePk.builder()
                        .providerId(providerId)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .build())
                .build();

        LastUpdatedEntity lastUpdatedEntity = LastUpdatedEntity.builder()
                .providerId(providerId)
                .lastUpdatedAt(lastUpdatedAt)
                .build();

        when(cardRangeRepository.findByPkProviderId(providerId)).thenReturn(List.of(cardRangeEntity));
        when(lastUpdatedRepository.findByProviderId(providerId)).thenReturn(Optional.of(lastUpdatedEntity));

        GetCardRangesResponse cardRanges = cardRangesStorageClient.getCardRanges(new GetCardRangesRequest(providerId));

        assertEquals(1, cardRanges.getCardRangesSize());
        assertEquals(rangeStart, cardRanges.getCardRanges().get(0).getRangeStart());
        assertEquals(rangeEnd, cardRanges.getCardRanges().get(0).getRangeEnd());
        assertEquals(providerId, cardRanges.getProviderId());
        assertEquals(TypeUtil.temporalToString(lastUpdatedAt), cardRanges.getLastUpdatedAt());
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
}

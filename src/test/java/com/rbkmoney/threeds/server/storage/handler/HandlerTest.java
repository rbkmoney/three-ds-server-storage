package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.threeds.server.storage.ThreeDsServerStorageApplication;
import com.rbkmoney.threeds.server.storage.config.TestConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.ChallengeFlowTransactionInfoRepository;
import com.rbkmoney.threeds.server.storage.repository.LastUpdatedRepository;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {ThreeDsServerStorageApplication.class, TestConfig.class},
        webEnvironment = RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class,
                JdbcTemplateAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                FlywayAutoConfiguration.class,
        }
)
@TestPropertySource("classpath:application.yml")
@ContextConfiguration(initializers = HandlerTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HandlerTest {

    @MockBean
    private ChallengeFlowTransactionInfoRepository challengeFlowTransactionInfoRepository;

    @MockBean
    private CardRangeRepository cardRangeRepository;

    @MockBean
    private SerialNumRepository serialNumRepository;

    @MockBean
    private LastUpdatedRepository lastUpdatedRepository;

    @Value("${local.server.port}")
    private int port;

    private static final int TIMEOUT = 555000;

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

    @Test(expected = GetCardRangesNotFound.class)
    public void shouldThrowWhenEmptyCardRangesTest() throws Exception {
        String providerId = "visa";

        when(cardRangeRepository.findByPkProviderId(providerId)).thenReturn(Collections.emptyList());

        cardRangesStorageClient.getCardRanges(new GetCardRangesRequest(providerId));
    }

    @Test(expected = GetCardRangesNotFound.class)
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
                .acsDecConInd("acs")
                .deviceChannel("device")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .build();

        when(challengeFlowTransactionInfoRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(challengeFlowTransactionInfoEntity));

        ChallengeFlowTransactionInfo challengeFlowTransactionInfo = challengeFlowTransactionInfoStorageClient.getChallengeFlowTransactionInfo(transactionId);
        assertEquals(transactionId, challengeFlowTransactionInfo.getTransactionId());
    }

    public static class Initializer extends ConfigFileApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            super.initialize(configurableApplicationContext);
        }
    }
}

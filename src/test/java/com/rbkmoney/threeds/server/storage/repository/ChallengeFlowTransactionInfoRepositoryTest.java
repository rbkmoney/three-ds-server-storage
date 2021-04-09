package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class ChallengeFlowTransactionInfoRepositoryTest extends AbstractDaoConfig {

    @Autowired
    private ChallengeFlowTransactionInfoRepository repository;

    @Before
    public void setUp() {
        ChallengeFlowTransactionInfoEntity trap = ChallengeFlowTransactionInfoEntity.builder()
                .transactionId("TRAP")
                .deviceChannel("channel")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .acsDecConInd("dec")
                .providerId("1")
                .messageVersion("2.1.0")
                .acsUrl("1")
                .eci("eci")
                .authenticationValue("value")
                .build();

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetChallengeFlowTransactionInfo() {
        // Given
        ChallengeFlowTransactionInfoEntity entity = ChallengeFlowTransactionInfoEntity.builder()
                .transactionId("1")
                .deviceChannel("device")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .acsDecConInd("acs")
                .providerId("1")
                .messageVersion("2.1.0")
                .acsUrl("1")
                .eci("eci")
                .authenticationValue("value")
                .build();

        // When
        repository.save(entity);
        Optional<ChallengeFlowTransactionInfoEntity> saved = repository.findById("1");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(entity);
    }

    @Test
    public void shouldSaveAndGetChallengeFlowTransactionInfoWithNoAcsDecConInd() {
        // Given
        ChallengeFlowTransactionInfoEntity entity = ChallengeFlowTransactionInfoEntity.builder()
                .transactionId("2")
                .deviceChannel("device")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .acsDecConInd(null)
                .providerId("1")
                .messageVersion("2.1.0")
                .acsUrl("1")
                .eci("eci")
                .authenticationValue("value")
                .build();

        // When
        repository.save(entity);
        Optional<ChallengeFlowTransactionInfoEntity> saved = repository.findById("2");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(entity);
    }
}

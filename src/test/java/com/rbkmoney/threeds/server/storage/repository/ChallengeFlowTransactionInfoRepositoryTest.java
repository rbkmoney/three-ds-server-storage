package com.rbkmoney.threeds.server.storage.repository;

import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.ChallengeFlowTransactionInfoEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
public class ChallengeFlowTransactionInfoRepositoryTest extends AbstractDaoConfig {

    @Autowired
    private ChallengeFlowTransactionInfoRepository repository;

    @Before
    public void setUp() {
        ChallengeFlowTransactionInfoEntity trap = ChallengeFlowTransactionInfoEntity.builder()
                .transactionId("TRAP")
                .acsDecConInd("dec")
                .deviceChannel("channel")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .build();

        repository.save(trap);
    }

    @Test
    public void shouldSaveAndGetChallengeFlowTransactionInfo() {
        // Given
        ChallengeFlowTransactionInfoEntity entity = ChallengeFlowTransactionInfoEntity.builder()
                .transactionId("1")
                .acsDecConInd("acs")
                .deviceChannel("device")
                .decoupledAuthMaxTime(LocalDateTime.now())
                .build();

        // When
        repository.save(entity);
        Optional<ChallengeFlowTransactionInfoEntity> saved = repository.findByTransactionId("1");

        // Then
        assertTrue(saved.isPresent());
        assertThat(saved.get()).isEqualTo(entity);
    }
}

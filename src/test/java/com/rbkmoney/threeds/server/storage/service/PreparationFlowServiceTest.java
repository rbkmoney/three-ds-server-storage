package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.error.ErrorCode;
import com.rbkmoney.threeds.server.domain.root.emvco.ErroWrapper;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import com.rbkmoney.threeds.server.serialization.EnumWrapper;
import com.rbkmoney.threeds.server.storage.client.ThreeDsClient;
import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.CardRangePk;
import com.rbkmoney.threeds.server.storage.entity.SerialNumberEntity;
import com.rbkmoney.threeds.server.storage.mapper.LastUpdatedMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.LastUpdatedRepository;
import com.rbkmoney.threeds.server.storage.repository.SerialNumberRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PreparationFlowServiceTest extends AbstractDaoConfig {

    private static final String PROVIDER_ID = "TRAP";
    private static final String MESSAGE_VERSION = "2.1.0";
    private static final String SERIAL_NUMBER = "trap";

    @Autowired
    private PreparationFlowService preparationFlowService;

    @Autowired
    private CardRangeRepository cardRangeRepository;

    @Autowired
    private SerialNumberRepository serialNumberRepository;

    @Autowired
    private LastUpdatedRepository lastUpdatedRepository;

    @Autowired
    private LastUpdatedMapper lastUpdatedMapper;

    @MockBean
    private ThreeDsClient threeDsClient;

    @Before
    public void setUp() {
        cardRangeRepository.save(entity(PROVIDER_ID, 1L, 9L));
        lastUpdatedRepository.save(lastUpdatedMapper.toEntity(PROVIDER_ID));
        serialNumberRepository.save(entity());

        assertThat(cardRangeRepository.findAll()).hasSize(1);
        assertThat(lastUpdatedRepository.findAll()).hasSize(1);
        assertThat(serialNumberRepository.findAll()).hasSize(1);
    }

    @Test
    public void shouldSkipValidResponseTest() {
        when(threeDsClient.preparationFlow(anyString(), anyString(), anyString())).thenReturn(
                RBKMoneyPreparationResponse.builder()
                        .providerId("1")
                        .build());

        preparationFlowService.init(PROVIDER_ID, MESSAGE_VERSION);

        assertThat(cardRangeRepository.findAll()).hasSize(1);
        assertThat(lastUpdatedRepository.findAll()).hasSize(1);
        assertThat(serialNumberRepository.findAll()).hasSize(1);
    }

    @Test
    public void shouldSkipErrorButValidResponseTest() {
        when(threeDsClient.preparationFlow(anyString(), anyString(), anyString())).thenReturn(
                ErroWrapper.builder()
                        .errorCode(errorCode(ErrorCode.SENT_MESSAGE_LIMIT_EXCEEDED_103))
                        .build());

        preparationFlowService.init(PROVIDER_ID, MESSAGE_VERSION);

        assertThat(cardRangeRepository.findAll()).hasSize(1);
        assertThat(lastUpdatedRepository.findAll()).hasSize(1);
        assertThat(serialNumberRepository.findAll()).hasSize(1);
    }

    @Test
    public void shouldDeleteEntitiesAtErrorResponseTest() {
        when(threeDsClient.preparationFlow(any(), any(), any()))
                .thenReturn(
                        ErroWrapper.builder()
                                .errorCode(errorCode(ErrorCode.TRANSACTION_TIMED_OUT_402))
                                .build());

        preparationFlowService.init(PROVIDER_ID, MESSAGE_VERSION);

        assertThat(cardRangeRepository.findAll()).hasSize(0);
        assertThat(lastUpdatedRepository.findAll()).hasSize(0);
        assertThat(serialNumberRepository.findAll()).hasSize(0);
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

    private SerialNumberEntity entity() {
        return SerialNumberEntity.builder()
                .providerId(PROVIDER_ID)
                .serialNumber(SERIAL_NUMBER)
                .build();
    }

    private EnumWrapper<ErrorCode> errorCode(ErrorCode errorCode) {
        EnumWrapper<ErrorCode> errorCodeEnumWrapper = new EnumWrapper<>();
        errorCodeEnumWrapper.setValue(errorCode);
        return errorCodeEnumWrapper;
    }
}

package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.LastUpdatedEntity;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.LastUpdatedRepository;
import com.rbkmoney.threeds.server.storage.service.PreparationFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final PreparationFlowService preparationFlowService;
    private final CardRangeRepository cardRangeRepository;
    private final CardRangeMapper cardRangeMapper;
    private final LastUpdatedRepository lastUpdatedRepository;

    @Override
    public void initRBKMoneyPreparationFlow(InitRBKMoneyPreparationFlowRequest request) {
        log.info("Init RBK.money preparation flow for providerId={}", request.getProviderId());
        preparationFlowService.init(request.getProviderId());
    }

    @Override
    public GetCardRangesResponse getCardRanges(GetCardRangesRequest request) throws GetCardRangesNotFound, TException {
        String providerId = request.getProviderId();

        List<CardRange> cardRanges = getCardRanges(providerId);

        String lastUpdatedAt = getLastUpdatedAt(providerId);

        log.info("Return {} cardRanges for providerId={}, lastUpdatedAt={}", cardRanges.size(), providerId, lastUpdatedAt);

        return new GetCardRangesResponse()
                .setProviderId(providerId)
                .setCardRanges(cardRanges)
                .setLastUpdatedAt(lastUpdatedAt);
    }

    private List<CardRange> getCardRanges(String providerId) throws GetCardRangesNotFound {
        List<CardRangeEntity> entities = cardRangeRepository.findByPkProviderId(providerId);

        if (entities.isEmpty()) {
            throw new GetCardRangesNotFound("No value \"cardRanges\" present, providerId=" + providerId);
        }

        return entities.stream()
                .map(cardRangeMapper::fromEntityToThrift)
                .collect(toList());
    }

    private String getLastUpdatedAt(String providerId) throws GetCardRangesNotFound {
        LastUpdatedEntity lastUpdatedEntity = lastUpdatedRepository.findByProviderId(providerId)
                .orElse(null);

        if (lastUpdatedEntity == null) {
            throw new GetCardRangesNotFound("No value \"lastUpdatedAt\" present, providerId=" + providerId);
        }

        return TypeUtil.temporalToString(lastUpdatedEntity.getLastUpdatedAt());
    }
}

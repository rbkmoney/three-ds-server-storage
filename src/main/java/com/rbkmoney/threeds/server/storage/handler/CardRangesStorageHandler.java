package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.service.RBKMoneyPreparationFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final RBKMoneyPreparationFlowService preparationFlowService;
    private final CardRangeRepository repository;
    private final CardRangeMapper mapper;

    @Override
    public void initRBKMoneyPreparationFlow(InitRBKMoneyPreparationFlowRequest request) {
        preparationFlowService.init(request.getProviderId());
    }

    @Override
    public GetCardRangesResponse getCardRanges(GetCardRangesRequest request) {
        String providerId = request.getProviderId();
        List<CardRangeEntity> entities = repository.findByPkProviderId(providerId);

        List<CardRange> cardRanges = entities.stream()
                .map(mapper::toDomain)
                .collect(toList());

        return new GetCardRangesResponse()
                .setProviderId(providerId)
                .setCardRanges(cardRanges);
    }
}

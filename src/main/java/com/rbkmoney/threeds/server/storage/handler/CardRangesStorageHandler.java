package com.rbkmoney.threeds.server.storage.handler;

import com.rbkmoney.damsel.three_ds_server_storage.*;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.mapper.CardRangeMapper;
import com.rbkmoney.threeds.server.storage.repository.CardRangesRepository;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CardRangesStorageHandler implements CardRangesStorageSrv.Iface {

    private final CardRangesRepository repository;
    private final CardRangeMapper mapper;

    @Override
    public void initRBKMoneyPreparationFlow(InitRBKMoneyPreparationFlowRequest initRBKMoneyPreparationFlowRequest) throws TException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public GetCardRangesResponse getCardRanges(GetCardRangesRequest getCardRangesRequest) {
        String providerId = getCardRangesRequest.getProviderId();
        List<CardRangeEntity> entities = repository.findByProviderId(providerId);

        List<CardRange> cardRanges = entities.stream()
                .map(mapper::toDomain)
                .collect(toList());

        // TODO [a.romanov]: lastUpdatedAt?
        return new GetCardRangesResponse()
                .setProviderId(providerId)
                .setCardRanges(cardRanges);
    }
}

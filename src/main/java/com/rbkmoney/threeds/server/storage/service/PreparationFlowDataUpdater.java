package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.rbkmoney.cardrange.RBKMoneyCardRange;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreparationFlowDataUpdater {

    private final SerialNumService serialNumService;
    private final CardRangeService cardRangeService;
    private final LastUpdatedService lastUpdatedService;

    public void update(RBKMoneyPreparationResponse response) {
        String providerId = response.getProviderId();
        String serialNum = response.getSerialNum();
        List<RBKMoneyCardRange> domainCardRanges = response.getCardRanges();

        log.info(
                "Update preparation flow data for providerId={}, serialNum={}, cardRanges={}",
                providerId,
                serialNum,
                domainCardRanges.size());

        cardRangeService.saveAll(providerId, domainCardRanges);
        cardRangeService.deleteAll(providerId, domainCardRanges);
        serialNumService.save(providerId, serialNum);
        lastUpdatedService.save(providerId);
    }
}

package com.rbkmoney.threeds.server.storage.client;

import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ThreeDsServerClient {

    private final RestTemplate restTemplate;

    @Value("${client.three-ds-server.url}")
    private String url;

    public RBKMoneyPreparationResponse preparationFlow(RBKMoneyPreparationRequest request) {
        return restTemplate.postForEntity(
                url,
                request,
                RBKMoneyPreparationResponse.class)
                .getBody();
    }
}

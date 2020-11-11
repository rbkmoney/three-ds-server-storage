package com.rbkmoney.threeds.server.storage.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.base.Charsets;
import com.rbkmoney.threeds.server.storage.config.AbstractDaoConfig;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import io.micrometer.core.instrument.util.IOUtils;
import org.awaitility.Awaitility;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "client.three-ds-server.url=http://127.0.0.1:8089/"
})
public class PreparationFlowServiceTest extends AbstractDaoConfig {

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(8089);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private SerialNumRepository serialNumRepository;

    @Autowired
    private CardRangeRepository cardRangeRepository;

    @Autowired
    private PreparationFlowService preparationFlowService;

    @Test
    public void shouldSaveUpdatedSerialNumAndCardRanges() throws IOException {
        // Given
        stubForOkResponse();

        Awaitility.setDefaultPollDelay(Duration.ofSeconds(3));
        Awaitility.setDefaultTimeout(Duration.ofMinutes(1L));

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> Future = executorService.submit(() -> preparationFlowService.init("1", "2.1.0"));

        await().until(Future::isDone);

        Optional<SerialNumEntity> serialNum = serialNumRepository.findByProviderId("1");
        assertTrue(serialNum.isPresent());
        assertThat(serialNum.get().getSerialNum()).isEqualTo("20190411083623719000");

        List<CardRangeEntity> cardRanges = cardRangeRepository.findByPkProviderId("1");
        assertThat(cardRanges).hasSize(20);
    }

    private void stubForOkResponse() throws IOException {
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(readStringFromFile("preparation-flow-response-1.json"))));
    }

    private String readStringFromFile(String fileName) throws IOException {
        return IOUtils.toString(
                resourceLoader.getResource("classpath:__files/" + fileName).getInputStream(),
                Charsets.UTF_8);
    }
}

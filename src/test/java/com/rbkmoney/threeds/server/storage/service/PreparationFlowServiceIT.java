package com.rbkmoney.threeds.server.storage.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.base.Charsets;
import com.rbkmoney.threeds.server.storage.entity.CardRangeEntity;
import com.rbkmoney.threeds.server.storage.entity.SerialNumEntity;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.PostgresRepositoryTest;
import com.rbkmoney.threeds.server.storage.repository.SerialNumRepository;
import io.micrometer.core.instrument.util.IOUtils;
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
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "client.three-ds-server.url=http://127.0.0.1:8089/")
public class PreparationFlowServiceIT extends PostgresRepositoryTest {

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

        // When
        preparationFlowService.init("1");

        // Then
        Optional<SerialNumEntity> serialNum = serialNumRepository.findByProviderId("1");
        assertTrue(serialNum.isPresent());
        assertThat(serialNum.get().getSerialNum()).isEqualTo("20190411083623719000");

        List<CardRangeEntity> cardRanges = cardRangeRepository.findByPkProviderId("1");
        assertThat(cardRanges).hasSize(20);
    }

    @Test
    public void shouldRetryOnException() throws IOException {
        // Given
        stubWithErrorAndThenOkResponse();

        // When
        preparationFlowService.init("2");

        // Then
        Optional<SerialNumEntity> serialNum = serialNumRepository.findByProviderId("2");
        assertTrue(serialNum.isPresent());
        assertThat(serialNum.get().getSerialNum()).isEqualTo("20190411083623719000");

        List<CardRangeEntity> cardRanges = cardRangeRepository.findByPkProviderId("2");
        assertThat(cardRanges).hasSize(3);

    }

    private void stubForOkResponse() throws IOException {
        stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(readStringFromFile("preparation-flow-response-1.json"))));
    }

    private void stubWithErrorAndThenOkResponse() throws IOException {
        stubFor(post(urlEqualTo("/"))
                .inScenario("preparation")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .willSetStateTo("OK"));

        stubFor(post(urlEqualTo("/"))
                .inScenario("preparation")
                .whenScenarioStateIs("OK")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(readStringFromFile("preparation-flow-response-2.json"))));
    }

    private String readStringFromFile(String fileName) throws IOException {
        return IOUtils.toString(
                resourceLoader.getResource("classpath:__files/" + fileName).getInputStream(),
                Charsets.UTF_8);
    }
}

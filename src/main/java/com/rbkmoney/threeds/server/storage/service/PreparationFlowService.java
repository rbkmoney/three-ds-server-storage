package com.rbkmoney.threeds.server.storage.service;

import com.rbkmoney.threeds.server.domain.root.Message;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationRequest;
import com.rbkmoney.threeds.server.domain.root.rbkmoney.RBKMoneyPreparationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreparationFlowService {

    @Value("${client.three-ds-server.url}")
    private String url;

    private final SerialNumService serialNumService;
    private final RestTemplate restTemplate;
    private final PreparationFlowDataUpdater dataUpdater;

    public void init(String providerId, String messageVersion) {
        String serialNum = serialNumService.get(providerId).orElse(null);

        RBKMoneyPreparationRequest rbkMoneyPreparationRequest = RBKMoneyPreparationRequest.builder()
                .providerId(providerId)
                .serialNum(serialNum)
                .build();
        rbkMoneyPreparationRequest.setMessageVersion(messageVersion);

        log.info("ASYNC request to 'three-ds-server' service: request={}", rbkMoneyPreparationRequest.toString());
        Message message = restTemplate.postForObject(url, rbkMoneyPreparationRequest, Message.class);
        if ((!(message instanceof RBKMoneyPreparationResponse))) {
            log.warn("Un support type {}", message);
        } else {
            RBKMoneyPreparationResponse rbkMoneyPreparationResponse = (RBKMoneyPreparationResponse) message;

            log.info("ASYNC response from 'three-ds-serve'r service: response={}", rbkMoneyPreparationResponse.toString());

            dataUpdater.update(rbkMoneyPreparationResponse);
        }

// todo 5
//        Executors.newSingleThreadExecutor().submit(() -> {
//        });

//todo 4 @Async + pool size


//todo 3 асинхронный запрос
//        Disposable subscribe = WebClient.create()
//                .post()
//                .uri(URI.create(url))
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(rbkMoneyPreparationRequest)
//                .exchange()
//                .timeout(Duration.ofMillis(networkTimeout))
//                .flatMap(response -> response.bodyToMono(Message.class))
//                .subscribe(
//                        message -> {
//                            if ((!(message instanceof RBKMoneyPreparationResponse))) {
//                                log.warn("Un support type {}", message);
//                            } else {
//                                RBKMoneyPreparationResponse rbkMoneyPreparationResponse = (RBKMoneyPreparationResponse) message;
//                                log.info("ASYNC response from 'three-ds-serve'r service: response={}", rbkMoneyPreparationResponse.toString());
//                                dataUpdater.update(rbkMoneyPreparationResponse);
//                            }
//                        });


//todo 2 сихронный запрос
//        ResponseEntity<Message> response = restTemplate.postForEntity(url, rbkMoneyPreparationRequest, Message.class);
//        log.info(response.getBody().toString());


// todo 1 асинхронный запрос
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .version(HttpClient.Version.HTTP_1_1)
//                    .uri(URI.create(url))
//                    .timeout(Duration.ofMillis(networkTimeout))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(rbkMoneyPreparationRequest)))
//                    .build();
//            HttpClient.newBuilder().build()
//                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::body)
//                    .thenAccept(
//                            content -> {
//                                try {
//                                    RBKMoneyPreparationResponse rbkMoneyPreparationResponse = objectMapper.readValue(content, RBKMoneyPreparationResponse.class);
//                                    log.info("ASYNC response from 'three-ds-serve'r service: response={}", rbkMoneyPreparationResponse.toString());
//                                    dataUpdater.update(rbkMoneyPreparationResponse);
//                                } catch (JsonProcessingException e) {
//                                    throw new MessageTypeException(content, e);
//                                }
//                            });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}

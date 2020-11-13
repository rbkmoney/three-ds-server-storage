package com.rbkmoney.threeds.server.storage.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rbkmoney.threeds.server.domain.root.Message;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preparation")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final PreparationFlowService preparationFlowService;

    @PostMapping
    public ResponseEntity<Message> processMessage(@RequestBody PreparationRequestBody preparationRequestBody) {
        preparationFlowService.init(preparationRequestBody.getProviderId(), preparationRequestBody.getMessageVersion());
        return ResponseEntity.ok().build();
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @Builder
    @JsonInclude(value = JsonInclude.Include.NON_ABSENT)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PreparationRequestBody {
        private String providerId;
        private String messageVersion;
    }
}

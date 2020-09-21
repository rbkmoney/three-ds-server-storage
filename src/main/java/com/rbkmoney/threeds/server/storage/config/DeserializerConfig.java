package com.rbkmoney.threeds.server.storage.config;

import com.rbkmoney.damsel.three_ds_server_storage.InitRBKMoneyPreparationFlowRequest;
import com.rbkmoney.threeds.server.storage.deserializer.ThriftDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeserializerConfig {

    @Bean
    public ThriftDeserializer<InitRBKMoneyPreparationFlowRequest> preparationFlowRequestDeserializer() {
        return new ThriftDeserializer<>();
    }
}

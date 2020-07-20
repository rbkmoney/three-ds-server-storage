package com.rbkmoney.threeds.server.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.listener.RetryListenerSupport;

import java.util.List;

@Slf4j
@Configuration
public class RetryListenerConfig {

    @Bean
    public List<RetryListener> retryListeners() {
        return List.of(new RetryListenerSupport() {
            @Override
            public <T, E extends Throwable> void onError(
                    RetryContext context,
                    RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.warn("Retrying method={}, count={} because of the exception",
                        context.getAttribute("context.name"),
                        context.getRetryCount(),
                        throwable);
            }
        });
    }
}

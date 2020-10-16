package com.rbkmoney.threeds.server.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.web.client.MetricsRestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.time.Duration;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
            MetricsRestTemplateCustomizer metricsRestTemplateCustomizer,
            @Value("${client.three-ds-server.timeout}") int networkTimeout) {
        RestTemplate restTemplate = new org.springframework.boot.web.client.RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient()))
                .additionalCustomizers(metricsRestTemplateCustomizer)
                .setConnectTimeout(Duration.ofMillis(networkTimeout))
                .setReadTimeout(Duration.ofMillis(networkTimeout))
                .build();
        setMessageConverter(restTemplate);
        return restTemplate;
    }

    private CloseableHttpClient httpClient() {
        SSLContext sslContext = sslContext();

        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .disableAutomaticRetries()
                .build();
    }

    private SSLContext sslContext() {
        try {
            return new SSLContextBuilder().build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setMessageConverter(RestTemplate restTemplate) {
        for (HttpMessageConverter converter : restTemplate.getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setWriteAcceptCharset(false);
            }
        }
    }
}

package com.rbkmoney.threeds.server.storage.config;

import com.rbkmoney.woody.api.trace.ContextUtils;
import com.rbkmoney.woody.api.trace.context.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.web.client.MetricsRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    public SSLContext sslContext() throws NoSuchAlgorithmException, KeyManagementException {
        return new SSLContextBuilder().build();
    }

    @Bean
    public CloseableHttpClient httpClient(SSLContext sslContext) {
        return HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLContext(sslContext)
                .disableAutomaticRetries()
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory requestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(
            HttpComponentsClientHttpRequestFactory requestFactory,
            MetricsRestTemplateCustomizer metricsRestTemplateCustomizer) {
        return new RestTemplateBuilder()
                .requestFactory(() -> requestFactory)
                .additionalCustomizers(metricsRestTemplateCustomizer);
    }

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${client.three-ds-server.timeout}") int networkTimeout) {
        int executionTimeout = ContextUtils.getExecutionTimeout(
                TraceContext.getCurrentTraceData().getServiceSpan(),
                networkTimeout);

        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(executionTimeout))
                .setReadTimeout(Duration.ofMillis(executionTimeout))
                .build();

        setMessageConverter(restTemplate);
        return restTemplate;
    }

    private void setMessageConverter(RestTemplate restTemplate) {
        for (HttpMessageConverter converter : restTemplate.getMessageConverters()) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setWriteAcceptCharset(false);
            }
        }
    }
}

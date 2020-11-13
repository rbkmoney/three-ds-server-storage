package com.rbkmoney.threeds.server.storage.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
            @Value("${client.three-ds-server.readTimeout}") int readTimeout,
            @Value("${client.three-ds-server.connectTimeout}") int connectTimeout) {
        return new RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()))
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
//
//    @Bean
//    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(
//            @Value("${httpConnPool.maxTotal}") int maxTotal,
//            @Value("${httpConnPool.defaultMaxPerRoute}") int defaultMaxPerRoute) {
//        PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
//        result.setMaxTotal(maxTotal);
//        result.setDefaultMaxPerRoute(defaultMaxPerRoute);
//        return result;
//    }
//
//    @Bean
//    public RequestConfig requestConfig(@Value("${client.three-ds-server.timeout}") int networkTimeout) {
//        return RequestConfig.custom()
//                .setConnectionRequestTimeout(networkTimeout)
//                .setConnectTimeout(networkTimeout)
//                .setSocketTimeout(networkTimeout)
//                .build();
//    }
//
//    @Bean
//    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RequestConfig requestConfig) {
//        return HttpClientBuilder.create()
//                .setConnectionManager(poolingHttpClientConnectionManager)
//                .setDefaultRequestConfig(requestConfig)
//                .build();
//    }
//
//    @Bean
//    public RestTemplate restTemplate(@Value("${client.three-ds-server.timeout}") int networkTimeout, HttpClient httpClient) {
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setHttpClient(httpClient);
//        requestFactory.setReadTimeout(networkTimeout);
//        return new RestTemplate(requestFactory);
//    }
}

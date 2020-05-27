package com.rbkmoney.threeds.server.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRetry
@EnableAsync
@ServletComponentScan
@SpringBootApplication
public class ThreeDsServerStorageApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreeDsServerStorageApplication.class, args);
    }

}

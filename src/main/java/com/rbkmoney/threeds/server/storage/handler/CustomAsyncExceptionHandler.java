package com.rbkmoney.threeds.server.storage.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Method name - ").append(method.getName());
        for (Object param : obj) {
            stringBuilder.append("Parameter value - ").append(param);
        }
        log.warn(stringBuilder.toString(), throwable);
    }
}

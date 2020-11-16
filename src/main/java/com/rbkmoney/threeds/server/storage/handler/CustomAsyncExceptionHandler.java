package com.rbkmoney.threeds.server.storage.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        String params = stream(obj)
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
        log.warn("Uncaught exception in Async handler with methodName=" + method.getName() + ", params=" + params, throwable);
    }
}

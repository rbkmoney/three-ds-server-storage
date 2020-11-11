package com.rbkmoney.threeds.server.storage.exception;

public class MessageTypeException extends RuntimeException {
    public MessageTypeException() {
    }

    public MessageTypeException(String message) {
        super(message);
    }

    public MessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageTypeException(Throwable cause) {
        super(cause);
    }

    public MessageTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

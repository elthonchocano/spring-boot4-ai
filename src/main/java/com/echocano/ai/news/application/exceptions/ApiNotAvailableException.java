package com.echocano.ai.news.application.exceptions;

public class ApiNotAvailableException extends RuntimeException {

    public ApiNotAvailableException(String message) {
        super(message);
    }
}

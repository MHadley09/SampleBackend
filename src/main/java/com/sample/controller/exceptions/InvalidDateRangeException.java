package com.sample.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateRangeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidDateRangeException(String message) {
        super(message);
    }
}
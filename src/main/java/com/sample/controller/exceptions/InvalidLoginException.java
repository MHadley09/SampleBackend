package com.sample.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidLoginException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
package com.chocksaway.exception;

import org.springframework.http.HttpStatus;


public class ConnectionException extends Exception {
    public ConnectionException(HttpStatus badRequest, String errorMessage, Throwable err) {
        super(badRequest.getReasonPhrase()  + " " + errorMessage, err);
    }
}

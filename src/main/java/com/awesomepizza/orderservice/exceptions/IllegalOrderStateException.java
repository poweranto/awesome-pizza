package com.awesomepizza.orderservice.exceptions;

public class IllegalOrderStateException extends RuntimeException {

    public IllegalOrderStateException(String message) {
        super(message);
    }
}
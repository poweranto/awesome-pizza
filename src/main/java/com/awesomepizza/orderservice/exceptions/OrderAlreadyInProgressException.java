package com.awesomepizza.orderservice.exceptions;

public class OrderAlreadyInProgressException extends RuntimeException {

    public OrderAlreadyInProgressException(String message) {
        super(message);
    }
}
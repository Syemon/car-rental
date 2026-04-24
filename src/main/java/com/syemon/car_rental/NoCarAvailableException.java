package com.syemon.car_rental;

public class NoCarAvailableException extends RuntimeException {

    public NoCarAvailableException(String message) {
        super(message);
    }
}

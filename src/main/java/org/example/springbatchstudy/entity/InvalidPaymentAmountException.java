package org.example.springbatchstudy.entity;

public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException(String message) {
        super(message);
    }
}

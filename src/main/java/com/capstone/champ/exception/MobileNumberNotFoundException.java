package com.capstone.champ.exception;

public class MobileNumberNotFoundException extends RuntimeException{
    public MobileNumberNotFoundException(String phoneNumber) {
        super("Account with phone number: " + phoneNumber + " is not found");
    }
}

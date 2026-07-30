package com.capstone.champ.exception;

public class PhoneNumberNotFoundException extends RuntimeException{
    public PhoneNumberNotFoundException(String phoneNumber) {
        super("Account with phone number: " + phoneNumber + " is not found");
    }
}

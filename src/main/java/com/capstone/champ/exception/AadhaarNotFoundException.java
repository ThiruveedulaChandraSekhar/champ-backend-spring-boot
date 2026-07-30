package com.capstone.champ.exception;

public class AadhaarNotFoundException extends RuntimeException{
    public AadhaarNotFoundException(String aadhaar) {
        super("Account with aadhaar number: " + aadhaar + " is not found");
    }
}

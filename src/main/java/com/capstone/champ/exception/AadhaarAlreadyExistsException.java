package com.capstone.champ.exception;

public class AadhaarAlreadyExistsException extends RuntimeException{
    public AadhaarAlreadyExistsException(String aadhaar) {
        super("Account with aadhaar number: " + aadhaar + " already exists");
    }
}

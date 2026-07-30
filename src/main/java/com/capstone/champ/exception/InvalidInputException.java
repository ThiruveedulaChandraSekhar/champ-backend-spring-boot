package com.capstone.champ.exception;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message) {
        super("Input: " + message + " is invalid");
    }
}

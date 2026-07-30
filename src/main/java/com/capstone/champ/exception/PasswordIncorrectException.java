package com.capstone.champ.exception;

public class PasswordIncorrectException extends RuntimeException{
    public PasswordIncorrectException() {
        super("Entered password is incorrect");
    }
}

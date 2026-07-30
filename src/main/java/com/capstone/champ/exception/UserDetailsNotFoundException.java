package com.capstone.champ.exception;

public class UserDetailsNotFoundException extends RuntimeException{
    public UserDetailsNotFoundException() {
        super("Details of the user: not found");
    }
}

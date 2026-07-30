package com.capstone.champ.exception;

public class DoctorDetailsNotFoundException extends RuntimeException{
    public DoctorDetailsNotFoundException() {
        super("Details not found");
    }
}

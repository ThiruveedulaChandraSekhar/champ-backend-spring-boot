package com.capstone.champ.exception;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(Long id) {
        super("Medicine not found with id " + id);
    }

    public MedicineNotFoundException(String name) {
        super("Medicine not found with name " + name);
    }
}
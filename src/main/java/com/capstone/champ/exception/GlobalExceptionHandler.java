package com.capstone.champ.exception;

import com.capstone.champ.payload.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AadhaarAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> aadhaarAlreadyExistsExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AadhaarNotFoundException.class)
    public ResponseEntity<ExceptionResponse> aadhaarNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DoctorDetailsNotFoundException.class)
    public ResponseEntity<ExceptionResponse> doctorDetailsNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ExceptionResponse> invalidInputExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PasswordIncorrectException.class)
    public ResponseEntity<ExceptionResponse> passwordIncorrectExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<ExceptionResponse> phoneNumberNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserDetailsNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userDetailsNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }

}

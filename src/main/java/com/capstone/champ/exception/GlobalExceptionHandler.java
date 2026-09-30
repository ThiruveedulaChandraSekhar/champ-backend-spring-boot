package com.capstone.champ.exception;

import com.capstone.champ.payload.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AadhaarAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> aadhaarAlreadyExistsExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> emailAlreadyExistsExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.CONFLICT);
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
    @ExceptionHandler(MobileNumberNotFoundException.class)
    public ResponseEntity<ExceptionResponse> phoneNumberNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserDetailsNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userDetailsNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MedicineNotFoundException.class)
    public ResponseEntity<ExceptionResponse> medicineNotFoundExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MlServiceException.class)
    public ResponseEntity<ExceptionResponse> mlServiceExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ExceptionResponse(false, e.getMessage()), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> unreadableRequestHandler(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ExceptionResponse(false, "Request body is invalid"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> unexpectedExceptionHandler(Exception e) {
        logger.error("Unexpected request failure", e);
        return new ResponseEntity<>(new ExceptionResponse(false, "An unexpected server error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

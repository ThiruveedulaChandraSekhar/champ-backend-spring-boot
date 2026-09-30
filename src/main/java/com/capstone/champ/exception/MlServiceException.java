package com.capstone.champ.exception;

public class MlServiceException extends RuntimeException {
    public MlServiceException(String message) {
        super(message);
    }

    public MlServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class MlServiceValidationException extends MlServiceException {
        public MlServiceValidationException(String message) {
            super(message);
        }
    }

    public static class MlServiceUnavailableException extends MlServiceException {
        public MlServiceUnavailableException(String message) {
            super(message);
        }

        public MlServiceUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

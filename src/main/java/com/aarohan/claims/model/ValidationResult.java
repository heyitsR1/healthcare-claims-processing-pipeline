package com.aarohan.claims.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean isValid;
    private List<ValidationError> errors = new ArrayList<>();

    public ValidationResult() {
        this.errors = new ArrayList<>();
    }

    public static class ValidationError {
        private ValidationErrorCode errorCode;
        private String errorMessage;

        public ValidationError(ValidationErrorCode errorCode, String errorMesssage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public void addError(ValidationErrorCode errorCode, String errorMessage) {
        this.errors.add(new ValidationError(errorCode, errorMessage));
    }

    public boolean isValid() {
        return errors == null || errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return this.errors;
    }
}

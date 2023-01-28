package com.netcompany.exception;

import java.util.List;

import com.netcompany.dto.ValidationResult;

public class ValidationException extends Exception {
    private List<ValidationResult> validationMessage;

    public ValidationException(String message, List<ValidationResult> validationMessage) {
        super(message);
        this.validationMessage = validationMessage;
    }

    public List<ValidationResult> getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(List<ValidationResult> validationMessage) {
        this.validationMessage = validationMessage;
    }
}

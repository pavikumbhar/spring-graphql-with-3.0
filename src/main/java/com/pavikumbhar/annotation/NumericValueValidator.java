package com.pavikumbhar.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumericValueValidator implements ConstraintValidator<NumericValue, Number> {
    @Override
    public void initialize(NumericValue constraintAnnotation) {
        // Initialization, if needed
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values should be handled by other validators (e.g., @NotNull)
        }

        // Convert the number to a string representation
        String stringValue = String.valueOf(value);

        // Check if the string consists only of digits
        return isNumeric(stringValue);
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }
        return str.matches("\\d+"); // Use regex to check if the string consists only of digits
    }
}

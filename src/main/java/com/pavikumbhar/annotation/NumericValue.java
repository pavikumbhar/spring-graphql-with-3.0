package com.pavikumbhar.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumericValueValidator.class)
@Documented
public @interface NumericValue {
    String message() default "Value must be a numeric";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
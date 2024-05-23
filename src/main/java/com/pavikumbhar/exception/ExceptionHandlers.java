package com.pavikumbhar.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@Component
class ExceptionHandlers implements DataFetcherExceptionResolver {

    @Override
    public @NotNull Mono<List<GraphQLError>> resolveException(Throwable exception, final @NotNull DataFetchingEnvironment environment) {
        log.debug("Exception: {}", exception.getClass().getName());
        log.debug("Exception message : {}", exception.getMessage());

        if (exception instanceof ConstraintViolationException constraintViolationException) {
            final String joinedErrorMessage = constraintViolationException
                    .getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));

            return Mono.fromCallable(() -> Collections.singletonList(
                    GraphqlErrorBuilder.newError(environment)
                            .errorType(ErrorType.ValidationError)
                            .message(joinedErrorMessage)
                            .build()
            ));
        } else if (exception instanceof BindException bindException) {
            String errorMessage = getErrorMessage(bindException);
            return Mono.fromCallable(() -> Collections.singletonList(
                    GraphqlErrorBuilder.newError(environment)
                            .errorType(ErrorType.ValidationError)
                            .message(errorMessage)
                            .build()));
        }
        return Mono.empty();

    }

    @NotNull
    private String getErrorMessage(BindException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("Validation error(s): ");
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                errorMessage.append(String.format("Field '%s': %s %s; ", fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage()));
            } else {
                errorMessage.append(String.format("%s; ", error.getDefaultMessage()));
            }
        }
        return errorMessage.toString();
    }


}
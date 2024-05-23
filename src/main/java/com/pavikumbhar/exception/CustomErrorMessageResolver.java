package com.pavikumbhar.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author pavikumbhar
 */
@Slf4j
@Component
public class CustomErrorMessageResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        log.info("resolveToSingleError Exception: {}", ex.getClass().getName());
        log.info("resolveToSingleError message : {}", ex.getMessage());

        if (ex instanceof Exception resourceNotFoundException) {
            String message = resourceNotFoundException.getMessage();
            return GraphqlErrorBuilder.newError(env)
                 //   .errorType(CustomErrorType.INVALID_OPERATION)
                    .message(message)
                    .extensions(Map.of("errorCode", 100))
                    .build();
        }
        return null;
    }
}


package com.pavikumbhar.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LogInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        final String graphqlRequestLog = """
                
                ----- graphql request log interceptor ---
                headers: %s
                uri: %s
                id: %s
                document: %s
                execution id: %s
                operation name: %s
                variables: %s
                
                """.formatted(
                request.getHeaders().toSingleValueMap(),
                request.getUri(),
                request.getId(),
                request.getDocument(),
                request.getExecutionId(),
                request.getOperationName(),
                request.getVariables()
        );
        log.info(graphqlRequestLog);
        return chain.next(request);
    }


}
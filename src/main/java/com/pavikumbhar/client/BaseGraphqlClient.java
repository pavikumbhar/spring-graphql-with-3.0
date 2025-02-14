package com.pavikumbhar.client;


import com.pavikumbhar.dto.QueryResult;
import com.pavikumbhar.dto.QueryResultList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.HttpGraphQlClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

/**
 * @author pavikumbhar
 */
@Slf4j
public class BaseGraphqlClient {
    private final HttpGraphQlClient httpGraphQlClient;

    private final String url;
    private final long maxAttempts;
    private final Duration retryDelay;

    protected BaseGraphqlClient(HttpGraphQlClient httpGraphQlClient, String url, long maxAttempts, Duration retryDelay) {
        this.httpGraphQlClient = httpGraphQlClient;
        this.url = url;
        this.maxAttempts = maxAttempts;
        this.retryDelay = retryDelay;
    }

    protected Mono<HttpGraphQlClient> getHttpGraphQlClient(Map<String, String> headers) {
        return Mono.deferContextual(ctx -> Mono.just(httpGraphQlClient.mutate()
                .url(url)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add)).build()));
    }

    protected <T> Mono<QueryResult<T>> executeQueryByDocumentName(Map<String, String> headers, String documentName,
                                                                  Map<String, Object> variables, String path,
                                                                  ParameterizedTypeReference<T> entityType) {

        return getHttpGraphQlClient(headers)
                .flatMap(client -> {
                    Mono<ClientGraphQlResponse> clientGraphQlResponse = client
                            .documentName(documentName)
                            .variables(variables)
                            .execute();

                    return clientGraphQlResponse.map(graphQlResponse -> {
                        if (!graphQlResponse.isValid()) {
                            return QueryResult.<T>builder().errors(graphQlResponse.getErrors()).build();
                        }
                        ClientResponseField field = graphQlResponse.field(path);
                        if (!field.getErrors().isEmpty()) {
                            return QueryResult.<T>builder().errors(field.getErrors()).build();
                        } else {
                            return QueryResult.<T>builder().data(field.toEntity(entityType)).build();
                        }
                    });

                })
                .retryWhen(retry("executeQueryByDocumentName", maxAttempts, retryDelay));
    }

    protected <T> Mono<QueryResultList<T>> executeQueryListByDocumentName(Map<String, String> headers, String documentName,
                                                                          Map<String, Object> variables, String path,
                                                                          ParameterizedTypeReference<T> entityType) {

        return getHttpGraphQlClient(headers)
                .flatMap(client -> {
                    Mono<ClientGraphQlResponse> clientGraphQlResponse = client
                            .documentName(documentName)
                            .variables(variables).execute();

                    return clientGraphQlResponse.map(graphQlResponse -> {
                        if (!graphQlResponse.isValid()) {
                            return QueryResultList.<T>builder().errors(graphQlResponse.getErrors()).build();
                        }
                        ClientResponseField field = graphQlResponse.field(path);
                        if (!field.getErrors().isEmpty()) {
                            return QueryResultList.<T>builder().errors(field.getErrors()).build();
                        } else {
                            return QueryResultList.<T>builder().data(field.toEntityList(entityType)).build();
                        }
                    });

                })
                .retryWhen(retry("executeQueryListByDocumentName", maxAttempts, retryDelay));
    }


    private Retry retry(String message, long maxAttempts, Duration retryDelay) {
        return Retry.backoff(maxAttempts, retryDelay)
                .doBeforeRetry(retrySignal -> log.info("Retrying {} attempt [{}]", message, retrySignal.totalRetries() + 1))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        retrySignal.failure());
    }


}

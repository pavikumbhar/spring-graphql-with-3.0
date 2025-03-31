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
 * Base class for GraphQL client with retry mechanism and response handling.
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

    /**
     * Builds the HttpGraphQlClient with custom headers.
     * @param headers Request headers
     * @return Mono<HttpGraphQlClient>
     */
    protected Mono<HttpGraphQlClient> getHttpGraphQlClient(Map<String, String> headers) {
        return Mono.deferContextual(ctx -> Mono.just(
                httpGraphQlClient.mutate()
                        .url(url)
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .build()
        ));
    }

    /**
     * Executes a GraphQL query by document name and retrieves a single object.
     * @param headers Request headers
     * @param documentName GraphQL document name
     * @param variables Query variables
     * @param path Path to the desired field in the response
     * @param entityType Type reference for the response entity
     * @param <T> Entity type
     * @return Mono<QueryResult<T>>
     */
    protected <T> Mono<QueryResult<T>> executeQueryByDocumentName(
            Map<String, String> headers,
            String documentName,
            Map<String, Object> variables,
            String path,
            ParameterizedTypeReference<T> entityType
    ) {
        return getHttpGraphQlClient(headers)
                .flatMap(client -> client.documentName(documentName)
                        .variables(variables)
                        .execute()
                        .map(graphQlResponse -> processResponse(graphQlResponse, path, entityType))
                )
                .retryWhen(createRetry("executeQueryByDocumentName"));
    }

    /**
     * Executes a GraphQL query by document name and retrieves a list of objects.
     * @param headers Request headers
     * @param documentName GraphQL document name
     * @param variables Query variables
     * @param path Path to the desired field in the response
     * @param entityType Type reference for the response entity
     * @param <T> Entity type
     * @return Mono<QueryResultList<T>>
     */
    protected <T> Mono<QueryResultList<T>> executeQueryListByDocumentName(
            Map<String, String> headers,
            String documentName,
            Map<String, Object> variables,
            String path,
            ParameterizedTypeReference<T> entityType
    ) {
        return getHttpGraphQlClient(headers)
                .flatMap(client -> client.documentName(documentName)
                        .variables(variables)
                        .execute()
                        .map(graphQlResponse -> processListResponse(graphQlResponse, path, entityType))
                )
                .retryWhen(createRetry("executeQueryListByDocumentName"));
    }

    /**
     * Processes a single-object GraphQL response.
     * @param graphQlResponse GraphQL response
     * @param path Path to the desired field
     * @param entityType Type reference for the entity
     * @param <T> Entity type
     * @return QueryResult<T>
     */
    private <T> QueryResult<T> processResponse(ClientGraphQlResponse graphQlResponse, String path, ParameterizedTypeReference<T> entityType) {
        if (!graphQlResponse.isValid()) {
            return QueryResult.<T>builder().errors(graphQlResponse.getErrors()).build();
        }
        ClientResponseField field = graphQlResponse.field(path);
        if (!field.getErrors().isEmpty()) {
            return QueryResult.<T>builder().errors(field.getErrors()).build();
        }
        return QueryResult.<T>builder().data(field.toEntity(entityType)).build();
    }

    /**
     * Processes a list-object GraphQL response.
     * @param graphQlResponse GraphQL response
     * @param path Path to the desired field
     * @param entityType Type reference for the list of entities
     * @param <T> Entity type
     * @return QueryResultList<T>
     */
    private <T> QueryResultList<T> processListResponse(ClientGraphQlResponse graphQlResponse, String path, ParameterizedTypeReference<T> entityType) {
        if (!graphQlResponse.isValid()) {
            return QueryResultList.<T>builder().errors(graphQlResponse.getErrors()).build();
        }
        ClientResponseField field = graphQlResponse.field(path);
        if (!field.getErrors().isEmpty()) {
            return QueryResultList.<T>builder().errors(field.getErrors()).build();
        }
        return QueryResultList.<T>builder().data(field.toEntityList(entityType)).build();
    }

    /**
     * Creates a retry mechanism for GraphQL queries.
     * @param message Log message for retries
     * @return Retry instance
     */
    private Retry createRetry(String message) {
        return Retry.backoff(maxAttempts, retryDelay)
                .doBeforeRetry(retrySignal -> log.info("Retrying {} attempt [{}]", message, retrySignal.totalRetries() + 1))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }
}

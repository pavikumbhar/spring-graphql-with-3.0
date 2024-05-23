package com.pavikumbhar.client;

import com.pavikumbhar.dto.QueryResult;
import com.pavikumbhar.dto.QueryResultList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @Author pavikumbhar
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GraphQlClient {

    private final HttpGraphQlClient httpGraphQlClient;

    public <T> Mono<QueryResult<T>> getGraphQLQueryResult(String document, String path, Map<String, Object> variables,
                                                          ParameterizedTypeReference<T> entityType) {
        log.info("getGraphQLQueryResult() document -> {} path: {}", document,path);
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.document(document)
                .variables(variables).execute();
        return handleResponse(clientGraphQlResponse, path, field -> field.toEntity(entityType));
    }


    public <T> Mono<QueryResult<T>> getGraphQLQueryResultByDocumentName(String documentName, String path, Map<String, Object> variables,
                                                                        ParameterizedTypeReference<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.documentName(documentName)
                .variables(variables).execute();
        return handleResponse(clientGraphQlResponse, path, field -> field.toEntity(entityType));
    }

    public <T> Mono<QueryResult<T>> getGraphQLQueryResultByDocumentName(String documentName, String path, Map<String, Object> variables,
                                                                        Class<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.documentName(documentName)
                .variables(variables).execute();
        return handleResponse(clientGraphQlResponse, path, field -> field.toEntity(entityType));
    }



    public <T> Mono<QueryResult<T>> getGraphQLQueryResultByDocumentName(String documentName, String operationName, String path, Map<String, Object> variables,
                                                                        ParameterizedTypeReference<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.documentName(documentName)
                .operationName(operationName)
                .variables(variables)
                .execute();
        return handleResponse(clientGraphQlResponse, path, field -> field.toEntity(entityType));
    }

    public <T> Mono<QueryResultList<T>> getGraphQLQueryResultListByDocumentName(String documentName, String path,
                                                                                Map<String, Object> variables,
                                                                                ParameterizedTypeReference<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.documentName(documentName)
                .variables(variables).execute();
        return handleResponseList(clientGraphQlResponse, path, field -> field.toEntityList(entityType));
    }

    public <T> Mono<QueryResultList<T>> getGraphQLQueryResultListByDocumentName(String documentName, String operationName, String path,
                                                                                Map<String, Object> variables,
                                                                                ParameterizedTypeReference<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.documentName(documentName)
                .operationName(operationName)
                .variables(variables)
                .execute();
        return handleResponseList(clientGraphQlResponse, path, field -> field.toEntityList(entityType));
    }


    public <T> Mono<QueryResultList<T>> getGraphQLQueryResultList(String document, String path,
                                                                  Map<String, Object> variables,
                                                                  ParameterizedTypeReference<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.document(document)
                .variables(variables)
                .execute();
        return handleResponseList(clientGraphQlResponse, path, field -> field.toEntityList(entityType));
    }


    public <T> Mono<QueryResult<T>> getGraphQLQueryResult(String document, String path, Map<String,
            Object> variables, Class<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.document(document)
                .variables(variables)
                .execute();
        return handleResponse(clientGraphQlResponse, path, field -> field.toEntity(entityType));
    }


    public <T> Mono<QueryResultList<T>> getGraphQLQueryResultList(String document, String path, Map<String,
            Object> variables, Class<T> entityType) {
        Mono<ClientGraphQlResponse> clientGraphQlResponse = httpGraphQlClient.document(document)
                .variables(variables)
                .execute();
        return handleResponseList(clientGraphQlResponse, path, field -> field.toEntityList(entityType));

    }


    private <T> Mono<QueryResultList<T>> handleResponseList(Mono<ClientGraphQlResponse> clientGraphQlResponse,
                                                            String path,
                                                            Function<ClientResponseField, List<T>> extractData) {
        return clientGraphQlResponse.map(graphQlResponse -> {
            if (!graphQlResponse.isValid()) {
                return QueryResultList.<T>builder().errors(graphQlResponse.getErrors()).build();
            }
            ClientResponseField field = graphQlResponse.field(path);
            if (!field.getErrors().isEmpty()) {
                return QueryResultList.<T>builder().errors(field.getErrors()).build();
            } else {
                return QueryResultList.<T>builder().data(extractData.apply(field)).build();
            }
        });
    }

    private <T> Mono<QueryResult<T>> handleResponse(Mono<ClientGraphQlResponse> clientGraphQlResponse,
                                                    String path, Function<ClientResponseField, T> extractData) {
        return clientGraphQlResponse.map(graphQlResponse -> {
            if (!graphQlResponse.isValid()) {
                return QueryResult.<T>builder().errors(graphQlResponse.getErrors()).build();
            }
            ClientResponseField field = graphQlResponse.field(path);
            if (!field.getErrors().isEmpty()) {
                return QueryResult.<T>builder().errors(field.getErrors()).build();
            } else {
                return QueryResult.<T>builder().data(extractData.apply(field)).build();
            }
        });
    }

    public <T> Mono<List<T>> executeQuery(final String queryName,
                                          final String path,
                                          final Class<T> responseClass,
                                          final Map<String, Object> variables) {
        log.debug("executeQuery {} query: {}", path, queryName);

        org.springframework.graphql.client.GraphQlClient.RequestSpec document = httpGraphQlClient.documentName(queryName);
        if (!CollectionUtils.isEmpty(variables)) document.variables(variables);

        return document.execute()
                .mapNotNull(response -> response.field(path).toEntityList(responseClass))
                .onErrorResume(error -> Mono.error(new RuntimeException("Error " + error.getMessage())));

    }

}

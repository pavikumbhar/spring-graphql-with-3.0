package com.pavikumbhar.client;


import com.pavikumbhar.dto.QueryResult;
import com.pavikumbhar.dto.QueryResultList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.HttpGraphQlClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class BaseGraphqlClient {
    private final HttpGraphQlClient httpGraphQlClient;

    private final String url;

    protected BaseGraphqlClient(HttpGraphQlClient httpGraphQlClient, String url) {
        this.httpGraphQlClient = httpGraphQlClient;
        this.url = url;
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

                });
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

                });
    }


}

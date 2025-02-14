package com.pavikumbhar.client;

import com.pavikumbhar.dto.QueryResult;
import com.pavikumbhar.dto.QueryResultList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * @author pavikumbhar
 */
@Slf4j
@Service
public class AppGraphQlClient extends BaseGraphqlClient {

    protected AppGraphQlClient(HttpGraphQlClient httpGraphQlClient,
                               @Value("${app.graphql.api.url}") String url,
                               @Value("${webclient.config.retry.max-attempts:1}") long maxAttempts,
                               @Value("${webclient.config.retry.delay:5s}") Duration retryDelay) {
        super(httpGraphQlClient, url, maxAttempts, retryDelay);
    }


    @Override
    public <T> Mono<QueryResult<T>> executeQueryByDocumentName(Map<String, String> headers, String documentName,
                                                               Map<String, Object> variables, String path,
                                                               ParameterizedTypeReference<T> entityType) {
        log.info("executeQueryByDocumentName() : documentName : {} , variables : {} path : {}", documentName, variables, path);
        return super.executeQueryByDocumentName(headers, documentName, variables, path, entityType);
    }

    @Override
    public <T> Mono<QueryResultList<T>> executeQueryListByDocumentName(Map<String, String> headers, String documentName,
                                                                       Map<String, Object> variables, String path,
                                                                       ParameterizedTypeReference<T> entityType) {
        log.info("executeQueryListByDocumentName() : documentName : {} , variables : {} path : {}", documentName, variables, path);
        return super.executeQueryListByDocumentName(headers, documentName, variables, path, entityType);
    }


}

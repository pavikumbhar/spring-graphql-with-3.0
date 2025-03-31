package com.pavikumbhar.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class AppConfiguration {

    /**
     * Creates an HttpGraphQlClient bean using the provided WebClient.
     * @param webClient Configured WebClient instance
     * @return HttpGraphQlClient instance
     */
    @Bean
    public HttpGraphQlClient httpGraphQlClient(WebClient webClient) {
        return HttpGraphQlClient.builder(webClient).build();
    }

    /**
     * Creates a WebClient bean with request and response logging filters.
     * @return Configured WebClient instance
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * Logs details about the outgoing HTTP request.
     * @return ExchangeFilterFunction for request logging
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                log.debug("Request Method: {}, URL: {}", clientRequest.method(), clientRequest.url());
                clientRequest.headers()
                        .forEach((name, values) -> values.forEach(value -> log.debug("Header: {} = {}", name, value)));
            }
            return Mono.just(clientRequest);
        });
    }

    /**
     * Logs details about the incoming HTTP response.
     * @return ExchangeFilterFunction for response logging
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                log.debug("Response Status: {}", clientResponse.statusCode());
                clientResponse.headers()
                        .asHttpHeaders()
                        .forEach((name, values) -> values.forEach(value -> log.debug("Header : {} = {}", name, value)));
            }
            return Mono.just(clientResponse);
        });
    }
}

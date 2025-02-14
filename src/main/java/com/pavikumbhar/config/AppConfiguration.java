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

    @Bean
    public HttpGraphQlClient httpGraphQlClient(WebClient webClient) {
        return HttpGraphQlClient.builder(webClient)
                .build();
    }


    @Bean
    public WebClient webClient() {
        return WebClient.builder().filter(logRequest()).build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                log.debug("logRequest : Request Method: {} url : {}", clientRequest.method(), clientRequest.url());
            }
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> {
                if (log.isDebugEnabled()) {
                    log.debug("{} : {}", name, value);
                }
            }));
            return Mono.just(clientRequest);
        });
    }


}

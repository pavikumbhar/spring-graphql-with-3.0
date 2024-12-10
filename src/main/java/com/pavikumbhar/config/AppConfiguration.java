package com.pavikumbhar.config;

import com.pavikumbhar.common.GenericRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
@EnableJpaRepositories(repositoryBaseClass = GenericRepositoryImpl.class)
public class AppConfiguration {

   @Bean
    public HttpGraphQlClient httpGraphQlClient() {

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/graphql")
                .build();
        return HttpGraphQlClient.builder(webClient)
                .build();
    }
}

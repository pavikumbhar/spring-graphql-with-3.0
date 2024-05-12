package com.pavikumbhar.config;

import com.pavikumbhar.config.scalar.LocalDateTimeScalar;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.Json)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Object)
                .scalar(LocalDateTimeScalar.LocalDateTimeType);
    }
}

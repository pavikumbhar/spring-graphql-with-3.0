package com.pavikumbhar.config.scalar;


import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.Value;
import graphql.schema.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * A custom scalar to allow passing of general Object classes into the GraphQL API
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectScalar {
    public static final GraphQLScalarType Object = GraphQLScalarType.newScalar()
            .name("Object")
            .description("a scalar to hold an Object")
            .coercing(new Coercing<>() {
                @Override
                public Object serialize(@NotNull final Object dataFetcherResult,
                                        @NotNull final GraphQLContext graphQLContext,
                                        @NotNull final Locale locale) throws CoercingSerializeException {
                    return dataFetcherResult;
                }

                @Override
                public Object parseValue(@NotNull final Object input,
                                         @NotNull final GraphQLContext graphQLContext,
                                         @NotNull final Locale locale) throws CoercingParseValueException {
                    return input;
                }

                @Override
                public Object parseLiteral(@NotNull final Value<?> input,
                                           @NotNull final CoercedVariables variables,
                                           @NotNull final GraphQLContext graphQLContext,
                                           @NotNull final Locale locale) throws CoercingParseLiteralException {
                    return input;
                }
            }).build();
}
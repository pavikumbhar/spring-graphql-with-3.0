package com.pavikumbhar.config.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimeScalar {

    public static final GraphQLScalarType LocalDateTimeType = GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("Represents Java LocalDateTime  object")
            .coercing(new Coercing<>() {
                @Override
                public Object serialize(@NotNull final Object dataFetcherResult,
                                        @NotNull final GraphQLContext graphQLContext,
                                        @NotNull final Locale locale) {
                    if (dataFetcherResult instanceof LocalDateTime localDateTime) {
                        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } else {
                        throw new CoercingSerializeException("Invalid value '" + dataFetcherResult + "' for LocalDateTime");
                    }
                }

                @Override
                public Object parseValue(@NotNull final Object input,
                                         @NotNull final GraphQLContext graphQLContext,
                                         @NotNull final Locale locale) {
                    if (input instanceof String) {
                        try {
                            return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid input for LocalDateTime: " + input, e);
                        }
                    }
                    throw new CoercingParseValueException("Expected a String value but was " + input.getClass().getSimpleName());
                }

                @Override
                public Object parseLiteral(@NotNull final Value<?> input,
                                           @NotNull final CoercedVariables variables,
                                           @NotNull final GraphQLContext graphQLContext,
                                           @NotNull final Locale locale) {
                    if (input instanceof StringValue stringValue) {
                        try {
                            return LocalDateTime.parse(stringValue.getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseLiteralException("Invalid input  for LocalDateTime: " + input, e);
                        }
                    }
                    throw new CoercingParseLiteralException("Invalid input for LocalDateTime: " + input);
                }
            })
            .build();
}
package com.pavikumbhar.config.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.*;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.stream.Collectors;

public final class _Any {
    public static final String typeName = "_Any";
    /**
     * Coercing logic for serializing/deserializing of _Any scalar
     */
    private static final Coercing<Object, Object> coercing =
            new Coercing<>() {
                @Override
                public Object serialize(
                        @NotNull Object dataFetcherResult,
                        @NotNull GraphQLContext graphQLContext,
                        @NotNull Locale locale)
                        throws CoercingSerializeException {
                    return dataFetcherResult;
                }

                @Override
                public Object parseValue(
                        @NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale)
                        throws CoercingParseValueException {
                    return input;
                }

                @Nullable
                @Override
                public Object parseLiteral(
                        @NotNull Value input,
                        @NotNull CoercedVariables variables,
                        @NotNull GraphQLContext graphQLContext,
                        @NotNull Locale locale)
                        throws CoercingParseLiteralException {
                    if (input instanceof NullValue) {
                        return null;
                    } else if (input instanceof FloatValue) {
                        return ((FloatValue) input).getValue();
                    } else if (input instanceof StringValue) {
                        return ((StringValue) input).getValue();
                    } else if (input instanceof IntValue) {
                        return ((IntValue) input).getValue();
                    } else if (input instanceof BooleanValue) {
                        return ((BooleanValue) input).isValue();
                    } else if (input instanceof EnumValue) {
                        return ((EnumValue) input).getName();
                    } else if (input instanceof ArrayValue) {
                        return ((ArrayValue) input)
                                .getValues().stream()
                                .map((value) -> parseLiteral(value, variables, graphQLContext, locale))
                                .collect(Collectors.toList());
                    } else if (input instanceof ObjectValue) {
                        return ((ObjectValue) input)
                                .getObjectFields().stream()
                                .collect(
                                        Collectors.toMap(
                                                ObjectField::getName,
                                                f -> parseLiteral(f.getValue(), variables, graphQLContext, locale)));
                    } else {
                        throw new CoercingParseLiteralException(
                                "Cannot parse input(" + input + ") to Any scalar");
                    }
                }
            };
    public static GraphQLScalarType type =
            GraphQLScalarType.newScalar().name(typeName).coercing(coercing).build();

    private _Any() {
        // hidden constructor
    }
}
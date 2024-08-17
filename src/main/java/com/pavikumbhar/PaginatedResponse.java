package com.pavikumbhar;

import graphql.GraphQLError;
import lombok.Data;

import java.util.List;
@Data
public class PaginatedResponse<T> {
    List<GraphQLError> errors;
    private List<T> items;
    private String nextCursor;
    private int totalElements;
}
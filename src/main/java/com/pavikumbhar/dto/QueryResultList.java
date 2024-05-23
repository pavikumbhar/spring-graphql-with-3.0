package com.pavikumbhar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.graphql.ResponseError;

import java.util.ArrayList;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResultList<T> {
    @Builder.Default
    private List<ResponseError> errors = new ArrayList<>();
    @Builder.Default
    private List<T> data = new ArrayList<>();
}

package com.pavikumbhar.common.specification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pavikumbhar.common.criteria.SearchFilter;
import com.pavikumbhar.common.exception.DataAccessException;
import jakarta.persistence.criteria.Selection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.Specification.where;

public class GenericSpecification<T> {

    public static <T> Specification<T> addMultiselect(Set<String> requestedFields) {
        return (root, query, criteriaBuilder) -> {
            List<Selection<?>> selections = requestedFields.stream()
                    .map(root::get)
                    .collect(toList());
            query.multiselect(selections);
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        };
    }

    public Specification<T> getSpecificationFromFilters(List<SearchFilter> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            return null;
        }
        Specification<T> specification = where(createSpecification(filters.remove(0)));
        for (SearchFilter input : filters) {
            specification = specification.and(createSpecification(input));
        }
        return specification;
    }

    public Specification<T> getSpecificationFromFilters2(List<SearchFilter> filters, Set<String> requestedFields) {
        if (CollectionUtils.isEmpty(filters)) {
            return null;
        }
        List<Specification<T>> specifications = filters.stream()
                .map(e -> createSpecification(e, requestedFields))
                .toList();
        Specification<T> combinedSpecification = specifications.get(0);

        for (int i = 1; i < specifications.size(); i++) {
            SearchFilter currentFilter = filters.get(i - 1);
            Specification<T> currentSpecification = specifications.get(i);

            combinedSpecification = switch (currentFilter.getLogicalOperator()) {
                case AND -> combinedSpecification.and(currentSpecification);
                case OR -> combinedSpecification.or(currentSpecification);
                default -> combinedSpecification;
            };
        }
        return combinedSpecification;
    }

    public Specification<T> createSpecification(SearchFilter filter) {

        return switch (filter.getOperator()) {

            case EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(filter.getField()),
                            castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));

            case NOT_EQ -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.notEqual(root.get(filter.getField()),
                            castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));

            case GREATER_THAN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.gt(root.get(filter.getField()),
                            (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));

            case LESS_THAN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.lt(root.get(filter.getField()),
                            (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));

            case LIKE -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(filter.getField()), "%" + filter.getValue() + "%");

            case IN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.in(root.get(filter.getField()))
                            .value(castToRequiredType1(root.get(filter.getField()).getJavaType(), filter.getValues()));
            default -> throw new DataAccessException("Operation not supported yet");
        };
    }

    private Object castToRequiredType(Class<?> fieldType, Object value) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        return objectMapper.convertValue(value, fieldType);
    }


    private Object castToRequiredType1(Class<?> fieldType, List<Object> value) {
        List<Object> lists = new ArrayList<>();
        for (Object s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }


    public Specification<T> createSpecification(SearchFilter filter, Set<String> requestedFields) {
        return (root, query, criteriaBuilder) -> {
            return switch (filter.getOperator()) {

                case EQUALS ->
                        criteriaBuilder.equal(root.get(filter.getField()), castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
                case NOT_EQ ->
                        criteriaBuilder.notEqual(root.get(filter.getField()), castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
                case GREATER_THAN ->
                        criteriaBuilder.gt(root.get(filter.getField()), (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
                case LESS_THAN ->
                        criteriaBuilder.lt(root.get(filter.getField()), (Number) castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue()));
                case LIKE ->
                        criteriaBuilder.like(criteriaBuilder.function("TO_CHAR", String.class, root.get(filter.getField())),
                                "%" + filter.getValue() + "%");
                case IN ->
                        criteriaBuilder.in(root.get(filter.getField())).value(castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValues()));
                default -> throw new UnsupportedOperationException("Operation not supported yet");
            };
        };
    }


}



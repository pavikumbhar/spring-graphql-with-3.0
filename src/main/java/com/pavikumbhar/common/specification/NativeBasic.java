package com.pavikumbhar.common.specification;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class NativeBasic {


    public <T> String getColumnName(String filedName, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Optional<Field> optionalField = Arrays.stream(fields)
                .filter(field -> field.getName().equalsIgnoreCase(filedName)).findAny();
        if (optionalField.isPresent()) {
            Column annotation = optionalField.get().getAnnotation(Column.class);
            return annotation.name();
        } else {
            throw new RuntimeException("Invalid field " + filedName);
        }
    }

    public <T> String getTableColumnNames(Class<T> clazz){
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> field.getAnnotation(Column.class).name())
                .collect(Collectors.joining(","));
    }


    /**
     * public Page<ExampleEntity> getCustomPaginatedResults(String name, int page, int size) {
     *     String query = "SELECT * FROM example_table WHERE name LIKE '" + name + "%'";
     *     String countQuery = "SELECT count(*) FROM example_table WHERE name LIKE '" + name + "%'";
     *     Pageable pageable = PageRequest.of(page, size);
     *
     *     return repository.executeNativeQuery(query, countQuery, pageable);
     * }
     */
}

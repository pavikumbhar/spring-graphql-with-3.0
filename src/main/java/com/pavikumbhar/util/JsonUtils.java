package com.pavikumbhar.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static <T> List<T> jsonToList(String jsonArray, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonArray, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return Collections.emptyList();
        }
    }


}

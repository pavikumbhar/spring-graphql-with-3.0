package com.pavikumbhar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static <T> List<T> jsonToList(String jsonArray, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(jsonArray, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (IOException e) {
          log.error("Exception occurred while converting jsonArray string  class type {}", e.getMessage(),e);
            return Collections.emptyList();
        }
    }

    // Serialize an object to JSON
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    // Deserialize JSON to an object
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to object", e);
        }
    }

    // Deserialize JSON to a generic Map
    public static Map<String, Object> fromJsonToMap(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to Map", e);
        }
    }

    // Deserialize JSON to a generic List
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to List", e);
        }
    }

    // Pretty print JSON
    public static String toPrettyJson(String json) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("Error pretty printing JSON", e);
        }
    }

    // Convert a Map to JSON
    public static String mapToJson(Map<String, Object> map) {
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing Map to JSON", e);
        }
    }

    // Convert a List to JSON
    public static <T> String listToJson(List<T> list) {
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing List to JSON", e);
        }
    }

    // Parse JSON string to JsonNode
    public static JsonNode parse(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON string to JsonNode", e);
        }
    }

    // Convert an object to a JsonNode
    public static JsonNode toJsonNode(Object obj) {
        return OBJECT_MAPPER.valueToTree(obj);
    }

    // Update an existing object with JSON data
    public static <T> T updateObject(String json, T obj) {
        try {
            return OBJECT_MAPPER.readerForUpdating(obj).readValue(json);
        } catch (IOException e) {
            throw new RuntimeException("Error updating object with JSON data", e);
        }
    }

    public <T> Map<String, Object> convertToMap(T object) {
        return OBJECT_MAPPER.convertValue(object, new TypeReference<Map<String, Object>>() {});
    }


}

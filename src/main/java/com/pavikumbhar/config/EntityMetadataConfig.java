package com.pavikumbhar.config;

import com.pavikumbhar.dto.DatabaseEntity;
import com.pavikumbhar.dto.EntityMetadata;
import com.pavikumbhar.util.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Configuration for loading entity metadata from a JSON string into a map.
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "database-entity")
public class EntityMetadataConfig {

    private Map<DatabaseEntity, EntityMetadata> entityMetadataMap;
    private String metadata;

    /**
     * Loads metadata from a JSON string into the entityMetadataMap.
     */
    private void loadMetadataMap() {
        if (metadata == null || metadata.isEmpty()) {
            log.error("Metadata is null or empty. Cannot load entity metadata.");
            throw new IllegalStateException("Metadata is null or empty");
        }

        try {
            List<EntityMetadata> entityMetadata = JsonUtils.jsonToList(metadata, EntityMetadata.class);
            entityMetadataMap = entityMetadata.stream()
                    .collect(Collectors.toMap(
                            e -> DatabaseEntity.getDatabaseEntity(e.getEntity()),
                            Function.identity()
                    ));
            log.info("Entity metadata loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load entity metadata from JSON: {}", e.getMessage());
            throw new RuntimeException("Error parsing metadata JSON", e);
        }
    }

    /**
     * Retrieves metadata for the specified entity.
     * @param entity The entity for which metadata is requested
     * @return The EntityMetadata corresponding to the entity
     */
    public EntityMetadata getMetadata(DatabaseEntity entity) {
        if (entityMetadataMap == null) {
            log.warn("Entity metadata map is null. Loading metadata map...");
            loadMetadataMap();
        }

        EntityMetadata entityMetadata = entityMetadataMap.get(entity);
        if (entityMetadata == null) {
            log.error("No metadata found for entity: {}", entity);
            throw new IllegalArgumentException("No metadata found for entity: " + entity);
        }

        return entityMetadata;
    }
}

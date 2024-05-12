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

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "database-entity")
public class EntityMetadataConfig {

    Map<DatabaseEntity, EntityMetadata> entityMetadataMap;
    private String metadata;

    private void loadMetadataMap() {
        List<EntityMetadata> entityMetadata = JsonUtils.jsonToList(metadata, EntityMetadata.class);
        entityMetadataMap = entityMetadata.stream().collect(Collectors.toMap(e -> DatabaseEntity.getDatabaseEntity(e.getEntity()), Function.identity()));
    }

    public EntityMetadata getMetadata(DatabaseEntity entity) {
        if (entityMetadataMap == null) {
            loadMetadataMap();
        }
        return entityMetadataMap.get(entity);
    }
}

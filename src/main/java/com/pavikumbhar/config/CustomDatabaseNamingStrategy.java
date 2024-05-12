package com.pavikumbhar.config;

import com.pavikumbhar.dto.DatabaseEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomDatabaseNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private final EntityMetadataConfig entityMetadataConfig;

    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment context) {
        return logicalName != null ? new Identifier(entityMetadataConfig.getMetadata(DatabaseEntity.getDatabaseEntity(logicalName.getText())).getSchema(), logicalName.isQuoted()) : null;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment context) {
        return logicalName != null ? new Identifier(entityMetadataConfig.getMetadata(DatabaseEntity.getDatabaseEntity(logicalName.getText())).getTable(), logicalName.isQuoted()) : null;
    }

}

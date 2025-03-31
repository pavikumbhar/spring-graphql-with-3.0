package com.pavikumbhar.config;

import com.pavikumbhar.dto.DatabaseEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.stereotype.Component;

/**
 * Custom naming strategy for Hibernate to define schema and table names based on metadata.
 */
@Component
@RequiredArgsConstructor
public class CustomDatabaseNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private final EntityMetadataConfig entityMetadataConfig;

    /**
     * Converts logical schema name to physical schema name using metadata configuration.
     * @param logicalName Logical schema name
     * @param context JDBC environment
     * @return Physical schema name as Identifier
     */
    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment context) {
        if (logicalName == null) {
            return null;
        }

        String schema = entityMetadataConfig.getMetadata(
                DatabaseEntity.getDatabaseEntity(logicalName.getText())).getSchema();

        return Identifier.toIdentifier(schema, logicalName.isQuoted());
    }

    /**
     * Converts logical table name to physical table name using metadata configuration.
     * @param logicalName Logical table name
     * @param context JDBC environment
     * @return Physical table name as Identifier
     */
    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment context) {
        if (logicalName == null) {
            return null;
        }

        String table = entityMetadataConfig.getMetadata(
                DatabaseEntity.getDatabaseEntity(logicalName.getText())).getTable();

        return Identifier.toIdentifier(table, logicalName.isQuoted());
    }
}

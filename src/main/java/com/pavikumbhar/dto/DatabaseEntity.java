package com.pavikumbhar.dto;

import com.pavikumbhar.common.exception.DataAccessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DatabaseEntity {
    OPERATING_SYSTEM("operatingSystem");

    private final String entity;

    public static DatabaseEntity getDatabaseEntity(String entity) {
        return Arrays.stream(DatabaseEntity.values()).filter(e -> e.getEntity().equals(entity))
                .findFirst().orElseThrow(() -> new DataAccessException(entity + " Entity not found"));
    }
}

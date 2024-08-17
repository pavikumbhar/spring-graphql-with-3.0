package com.pavikumbhar.repository;

import com.pavikumbhar.common.repository.GenericRepository;
import com.pavikumbhar.entity.OperatingSystem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.List;

@GraphQlRepository
public interface OperatingSystemRepository extends GenericRepository<OperatingSystem, Long> {

    @Query("SELECT o FROM OperatingSystem o WHERE (:after IS NULL OR o.id > :after) ORDER BY o.id ASC")
    List<OperatingSystem> findOperatingSystems(@Param("after") Long after, Pageable pageable);

    @Query("SELECT COUNT(o) FROM OperatingSystem o")
    int countTotalOperatingSystems();
}

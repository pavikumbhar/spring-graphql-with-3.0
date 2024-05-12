package com.pavikumbhar.repository;

import com.pavikumbhar.common.repository.GenericRepository;
import com.pavikumbhar.entity.OperatingSystem;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface OperatingSystemRepository extends GenericRepository<OperatingSystem, Long> {
}

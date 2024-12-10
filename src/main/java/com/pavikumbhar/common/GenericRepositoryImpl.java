package com.pavikumbhar.common;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.List;

public class GenericRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID>
        implements GenericRepository<T, ID> {

    private final EntityManager entityManager;

    public GenericRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                 EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Page<T> executeNativeQuery(String query, String countQuery, Pageable pageable) {
        List<T> content = entityManager.createNativeQuery(query, getDomainClass())
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = ((Number) entityManager.createNativeQuery(countQuery)
                .getSingleResult())
                .longValue();

        return new PageImpl<>(content, pageable, total);
    }
}

package com.pavikumbhar.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface GenericRepository<T, ID> extends JpaRepository<T, ID> {
    @Query(value = ":query",
            countQuery = ":countQuery",
            nativeQuery = true)
    Page<T> executeNativeQuery(@Param("query") String query,
                               @Param("countQuery") String countQuery,
                               Pageable pageable);
}

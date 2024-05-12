package com.pavikumbhar.common.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface GenericRepository<T, I> extends JpaRepository<T, I>, JpaSpecificationExecutor<T> {


    default List<T> getSpecByEnitityList(final Map<String, String> inputCriteria, List<String> fieldNames) {

        Specification<T> specifications = (root, query, criteriaBuilder) -> {


            return query.getRestriction();
        };

        return findAll(specifications);
    }

}

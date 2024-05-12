package com.pavikumbhar.common.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

@Slf4j
@RequiredArgsConstructor
public class GenericDao<T> {

    private final EntityManager entityManager;


    public <R> List<R> findAllWithPagination(Specification<T> specs,
                                             Class<R> projectionClass,
                                             Pageable pageable, Class<T> clazz) {
        Assert.notNull(projectionClass, "ProjectionClass must be not null!");
        Assert.notNull(pageable, "Pageable must be not null !");

        // Create query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> query = builder.createQuery(projectionClass);
        // Define FROM clause
        Root<T> root = applySpecToCriteria(query, builder, specs, clazz);
        // Define DTO projection
        List<Selection<?>> selections = getSelections(projectionClass, root);
        query.multiselect(selections);
        //Define ORDER BY clause
        applySorting(builder, query, root, pageable);
        return getPageableResultList(query, pageable);
    }


    public List<Tuple> findAllWithPagination(Specification<T> specs,
                                             Pageable pageable,
                                             List<String> fields, Class<T> clazz) {
        Assert.notNull(pageable, "Pageable must be not null!!");
        Assert.notEmpty(fields, "Fields must not be empty!");

        // Create query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        // Define FROM clause
        Root<T> root = applySpecToCriteria(query, builder, specs, clazz);
        // Define selecting expression
        List<Selection<?>> selections = getSelections(fields, root);
        query.multiselect(selections);
        //Define ORDER BY clause
        applySorting(builder, query, root, pageable);
        return getPageableResultList(query, pageable);
    }

    public List<T> findAllWithPagination1(Specification<T> specs,
                                          Pageable pageable,
                                          List<String> fields, Class<T> clazz) {
        Assert.notNull(pageable, "Pageable must be not null!");
        Assert.notEmpty(fields, "Fields must not be empty!");

        // Create query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        // Define FROM clause
        Root<T> root = applySpecToCriteria(query, builder, specs, clazz);
        // Define selecting expression
        List<Selection<?>> selections = getSelections(fields, root);
        query.multiselect(selections);
        //Define ORDER BY clause
        applySorting(builder, query, root, pageable);
        return getPageableResultList(query, pageable);
    }


    private <R> Root<T> applySpecToCriteria(CriteriaQuery<R> query,
                                            CriteriaBuilder builder,
                                            Specification<T> specs, Class<T> clazz) {
        Assert.notNull(query, "CriteriaQuery must not be null!");
        Root<T> root = query.from(clazz);
        if (specs == null) {
            return root;
        }
        Predicate predicate = specs.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
        }
        return root;
    }

    private <R> List<Selection<?>> getSelections(Class<R> projectionClass,
                                                 Root<T> root) {
        List<Selection<?>> selections = new ArrayList<>();
        ReflectionUtils.doWithFields(projectionClass,
                field -> selections.add(root.get(field.getName()).alias(field.getName())));
        return selections;
    }

    private List<Selection<?>> getSelections(List<String> fields,
                                             Root<T> root) {
        List<Selection<?>> selections = new ArrayList<>();
        for (String field : fields) {
            selections.add(root.get(field).alias(field));
        }
        return selections;
    }

    private <R> void applySorting(CriteriaBuilder builder,
                                  CriteriaQuery<R> query,
                                  Root<T> root,
                                  Pageable pageable) {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }
    }

    private <R> List<R> getPageableResultList(CriteriaQuery<R> query,
                                              Pageable pageable) {
        TypedQuery<R> typedQuery = entityManager.createQuery(query);
        // Apply pagination
        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        return typedQuery.getResultList();
    }

    public Page<T> getPage(CriteriaQuery<T> query,
                           Pageable pageable,
                           Specification<T> specs, Class<T> clazz) {
        Assert.notNull(pageable, "Pageable must not be null!");
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        // Apply page
        applyPagination(typedQuery, pageable);
        return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable,
                () -> getQueryCount(specs, clazz));
    }


    private long getQueryCount(Specification<T> specs, Class<T> clazz) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = applySpecToCriteria(query, builder, specs, clazz);
        query.select(builder.count(root));
        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        return typedQuery.getSingleResult();
    }

    private <R> void applyPagination(TypedQuery<R> typedQuery, Pageable pageable) {
        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
    }

    public Page<T> findAllWithPage(Specification<T> specs, Pageable pageable, List<String> fields, Class<T> clazz) {
        Assert.notNull(pageable, "Pageable must be not null!");
        // Create query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        // Define FROM clause
        Root<T> root = applySpecToCriteria(query, builder, specs, clazz);
        // Define selecting expression
        if (!CollectionUtils.isEmpty(fields)) {
            List<Selection<?>> selections = getSelections(fields, root);
            query.multiselect(selections);
        }

        //Define ORDER BY clause
        applySorting(builder, query, root, pageable);
        return getPage(query, pageable, specs, clazz);
    }


}


package com.pavikumbhar.common.criteria;


import com.pavikumbhar.common.repository.GenericRepository;
import com.pavikumbhar.common.specification.GenericDao;
import com.pavikumbhar.common.specification.GenericSpecification;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Author : pavikumbhar
 */
@Slf4j
@Service
public class QueryResolver {

    private final EntityManager entityManager;
    private final Repositories repositories;

    public QueryResolver(EntityManager entityManager, WebApplicationContext appContext) {
        this.entityManager = entityManager;
        this.repositories = new Repositories(appContext);
    }

    public <T, I> List<T> search(int page, int size, List<SearchFilter> filters, Sorting sorting, GenericRepository<T, I> genericRepository) {
        List<T> result = genericRepository.findAll((new GenericSpecification<T>()).getSpecificationFromFilters(filters), this.buildPageRequest(page, size, sorting)).getContent();
        log.info("Search for {} Repository", ProxyUtils.getUserClass(genericRepository));
        return result;

    }


    public <T, I> Page<T> searchPage(int page, int size, List<SearchFilter> filters, Sorting sorting, GenericRepository<T, I> genericRepository) {
        return genericRepository.findAll((new GenericSpecification<T>()).getSpecificationFromFilters(filters), this.buildPageRequest(page, size, sorting));

    }

    public PageRequest buildPageRequest(int page, int size, Sorting sorting) {
        if (Objects.isNull(sorting)) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(sorting.getOrder()), sorting.getField()));

    }

    public <T, I> List<T> search(int page, int size, List<SearchFilter> filters, Set<String> requestedFields, Sorting sorting, GenericRepository<T, I> genericRepository) {
        List<T> result = genericRepository.findAll((new GenericSpecification<T>()).getSpecificationFromFilters2(filters, requestedFields), this.buildPageRequest(page, size, sorting)).getContent();
        log.info("search for {} Repository", ProxyUtils.getUserClass(genericRepository));
        return result;

    }


    public <T, I> List<T> search(int page, int size, List<SearchFilter> filters, Set<String> requestedFields, Sorting sorting, Class<T> clazz) {
        GenericRepository<T, I> genericRepository = this.getRepository(clazz);
        List<T> result = genericRepository.findAll((new GenericSpecification<T>()).getSpecificationFromFilters2(filters, requestedFields), this.buildPageRequest(page, size, sorting)).getContent();
        log.info("search for {} Repository", clazz.getSimpleName());
        return result;

    }

    @SuppressWarnings("unchecked")
    private <T, I> GenericRepository<T, I> getRepository(Class<T> clazz) {
        return (GenericRepository<T, I>) repositories.getRepositoryFor(clazz)
                .orElseThrow(() -> new IllegalStateException("No repository found for type " + clazz.getName() + "!"));
    }


    public <T> List<T> findAllWithPagination(int page, int size, List<SearchFilter> filters, Set<String> requestedFields, Sorting sorting, Class<T> clazz) {
        Specification<T> specification = new GenericSpecification<T>().getSpecificationFromFilters2(filters, requestedFields);
        GenericDao<T> genericDao = new GenericDao<>(entityManager);
        return genericDao.findAllWithPagination1(specification, this.buildPageRequest(page, size, sorting), new ArrayList<>(requestedFields), clazz);

    }

    public <T> Page<T> findAllWithPage(int page, int size, List<SearchFilter> filters, Set<String> requestedFields, Sorting sorting, Class<T> clazz) {
        Specification<T> specification = new GenericSpecification<T>().getSpecificationFromFilters2(filters, requestedFields);
        GenericDao<T> genericDao = new GenericDao<>(entityManager);
        return genericDao.findAllWithPage(specification, this.buildPageRequest(page, size, sorting), new ArrayList<>(requestedFields), clazz);

    }


}

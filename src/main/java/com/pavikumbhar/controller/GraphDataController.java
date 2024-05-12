package com.pavikumbhar.controller;

import com.google.gson.Gson;
import com.pavikumbhar.common.criteria.QueryResolver;
import com.pavikumbhar.common.criteria.SearchFilter;
import com.pavikumbhar.common.criteria.Sorting;
import com.pavikumbhar.dto.GenericPaginationDTO;
import com.pavikumbhar.dto.OperatingSystemDTO;
import com.pavikumbhar.entity.OperatingSystem;
import com.pavikumbhar.repository.OperatingSystemRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author pavikumbhar
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GraphDataController {

    private final QueryResolver queryResolver;
    private final OperatingSystemRepository operatingSystemRepository;
    private final Gson gson;

    @QueryMapping
    public Flux<OperatingSystem> operatingSystems(@Argument int page,
                                                  @Argument int size,
                                                  @Argument List<SearchFilter> filters,
                                                  @Argument Sorting sorting,
                                                  DataFetchingEnvironment environment) {
        log.info("operatingSystems() : filters -> {}", gson.toJson(filters));
        Set<String> requestedFields = environment.getSelectionSet().getFields()
                .stream().map(SelectedField::getName).collect(Collectors.toSet());
        log.info("operatingSystems() : requestedFields -> {} ", requestedFields);
        List<OperatingSystem> search = queryResolver.search(page, size, filters, requestedFields, sorting, operatingSystemRepository);
        return Flux.fromIterable(search);
    }

    @MutationMapping
    public Mono<OperatingSystemDTO> createOperatingSystem(@Argument OperatingSystemDTO operatingSystem) {
        log.info("createOperatingSystem : operatingSystems ->  {}", operatingSystem);
        return Mono.just(operatingSystem);
    }

    @MutationMapping
    public Mono<OperatingSystem> updateOperatingSystem(@Argument OperatingSystem operatingSystem) {
        log.info("updateOperatingSystem : operatingSystems -> {}", operatingSystem);
        return Mono.just(operatingSystem);
    }

    @QueryMapping
    public Flux<OperatingSystem> operatingSystemsWithPagination(@Argument int page,
                                                                @Argument int size,
                                                                @Argument List<SearchFilter> filters,
                                                                @Argument Sorting sorting,
                                                                DataFetchingEnvironment environment) {
        log.info("operatingSystemsWithPagination() : filters -> {}", gson.toJson(filters));
        Set<String> requestedFields = environment.getSelectionSet()
                .getFields()
                .stream().map(SelectedField::getName)
                .collect(Collectors.toSet());
        log.info("operatingSystemsWithPagination() : requestedFields -> :{} ", requestedFields);
        Page<OperatingSystem> allWithPagination = queryResolver.findAllWithPage(page, size, filters, requestedFields, sorting, OperatingSystem.class);
        return Flux.fromIterable(allWithPagination.getContent());
    }

    @QueryMapping
    public Mono<GenericPaginationDTO<OperatingSystem>> operatingSystemsWithPage(@Argument int page,
                                                                                @Argument int size,
                                                                                @Argument List<SearchFilter> filters,
                                                                                @Argument Sorting sorting,
                                                                                DataFetchingEnvironment environment) {
        log.info("operatingSystemsWithPage() : filters -> {}", gson.toJson(filters));
        Set<String> requestedFields = getRequestedFields(environment);
        log.info("operatingSystemsWithPage() : requestedFields -> {} ", requestedFields);
        Page<OperatingSystem> operatingSystemPage = queryResolver.findAllWithPage(page, size, filters, requestedFields, sorting, OperatingSystem.class);
        GenericPaginationDTO<OperatingSystem> genericPaginationDTO = getGenericPaginationDTO(operatingSystemPage);
        return Mono.just(genericPaginationDTO);
    }

    public Set<String> getRequestedFields(DataFetchingEnvironment environment) {
        Optional<SelectedField> contentOptional = environment.getSelectionSet().getFields()
                .stream().filter(selectedField -> GenericPaginationDTO.Fields.content.equals(selectedField.getName()))
                .findFirst();
        return contentOptional.map(selectedField -> selectedField.getSelectionSet()
                        .getFields().stream().map(SelectedField::getName)
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    public <T> GenericPaginationDTO<T> getGenericPaginationDTO(Page<T> page) {
        return GenericPaginationDTO.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public <T, R> GenericPaginationDTO<R> getGenericPaginationDTO(Page<T> page, List<R> content) {
        return GenericPaginationDTO.<R>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public OperatingSystemDTO copyProperties(OperatingSystem source, OperatingSystemDTO target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    public List<OperatingSystemDTO> toOperatingSystemDto(List<OperatingSystem> operatingSystemList) {
        return operatingSystemList.stream().map(operatingSystem -> copyProperties(operatingSystem, new OperatingSystemDTO()))
                .toList();

    }

}

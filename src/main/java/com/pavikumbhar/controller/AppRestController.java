package com.pavikumbhar.controller;

import com.pavikumbhar.PaginatedGraphQlService;
import com.pavikumbhar.client.GraphQlClient;
import com.pavikumbhar.dto.GenericPaginationDTO;
import com.pavikumbhar.dto.QueryResult;
import com.pavikumbhar.dto.QueryResultList;
import com.pavikumbhar.entity.OperatingSystem;
import com.pavikumbhar.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppRestController {

    private final GraphQlClient graphQlClient;
    private final PaginatedGraphQlService paginatedGraphQlService;

    @GetMapping("load")
    public Mono<QueryResultList<OperatingSystem>> loadData() {

        // language=Graphql
        String document = """
                  query OperatingSystems {
                    operatingSystems {
                        id
                        name
                        version
                        kernel
                        usages
                        releaseDate
                        active
                    }
                }
                  """;
        Mono<QueryResultList<OperatingSystem>> operatingSystems = graphQlClient.getGraphQLQueryResultList(document, "operatingSystems", Map.of(), OperatingSystem.class);
        log.info("data loading .....................!!");
        return operatingSystems;

    }


    @GetMapping("upload")
    public Mono<QueryResult<OperatingSystem>> uploadData() {

        OperatingSystem system = OperatingSystem.builder()
                .name("Sampler name")
                .version("1.0")
                .kernel("5.16.11")
                .usages(90).active(true)
                .releaseDate(LocalDateTime.parse("2022-03-01T00:10:00.101"))
                .build();
        Map<String, Object> variables = JsonUtils.convertToMap(system);

        // language=Graphql
        String document = """
                 mutation CreateOperatingSystem($name:String,$version:String,$kernel:String,
                 $usages:String,$active:Boolean $releaseDate:LocalDateTime) {
                    createOperatingSystem(
                        operatingSystem: {
                            name:$name,
                            version:$version,
                            kernel:$kernel,
                            usages:$usages,
                            active:$active,
                            releaseDate:$releaseDate
                        }
                    ) {
                        id
                        name
                        version
                        kernel
                        usages
                        releaseDate
                        active
                    }
                }
                 """;

        return graphQlClient.getGraphQLQueryResult(document, "createOperatingSystem", variables, new ParameterizedTypeReference<OperatingSystem>() {
        });


    }



    @GetMapping("get-page-data")
    public Mono<QueryResult<GenericPaginationDTO<OperatingSystem>>> getPageData(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("page", page);
        variables.put("size", size);

        return graphQlClient.getGraphQLQueryResultByDocumentName("operating-systems-with-page",
                "operatingSystemsWithPage", variables, new ParameterizedTypeReference<GenericPaginationDTO<OperatingSystem>>() {
                });


    }

    @GetMapping("with-cursor")
    public Flux<OperatingSystem> withCursor() {

        return paginatedGraphQlService.operatingSystemsWithCursor();


    }


}

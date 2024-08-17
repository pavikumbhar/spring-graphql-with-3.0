package com.pavikumbhar;

import com.pavikumbhar.client.GraphQlClient;
import com.pavikumbhar.dto.QueryResult;
import com.pavikumbhar.entity.OperatingSystem;
import com.pavikumbhar.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaginatedGraphQlService {

    private final GraphQlClient graphQlClient;

    private static final int API_RATE_LIMIT = 100; // API allows 100 requests per minute
    private static final int TIME_PER_REQUEST_MS = 200; // 200 milliseconds per request
    private static final int SAFETY_MARGIN_PERCENTAGE = 20; // 20% safety margin

    public Flux<OperatingSystem> operatingSystemsWithCursor() {
        Set<String> cursorHistory = new HashSet<>();  // Track seen cursors
        int[] pageCount = {0};  // Track the number of pages fetched
        int[] maxPages = {0};   // Max pages calculated after the first response
        int[] iteration = {1}; // Track the current iteration or page number

        return fetchPage(1, null)  // Start with no cursor (fetch the first page)
                .expand(response -> {
                    // Check for errors
                    if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                        // Log the error and proceed to the next page
                        log.error("Iteration " + iteration[0] + " - Error received: " + response.getErrors());
                        // If errors are present, data is usually null or incomplete
                        return Flux.empty(); // Skip to the next page or handle accordingly
                    }

                    // Check if data is null
                    if (response.getData() == null) {
                        log.error("Iteration " + iteration[0] + " - Data is null due to an error or other issue.");
                        // Continue to the next page if available
                        return Flux.empty();  // Or you could return a Mono with default data
                    }

                    // On the first page, calculate MAX_PAGES based on totalItems and page size
                    if (pageCount[0] == 0) {
                        maxPages[0] = calculateMaxPages(response.getData().getTotalElements(), response.getData().getItems().size());
                    }

                    String nextCursor = response.getData().getNextCursor();
                    pageCount[0]++;
                    iteration[0]++; // Increment iteration for each page

                    if (nextCursor == null || cursorHistory.contains(nextCursor) || pageCount[0] >= maxPages[0]) {
                        return Flux.empty();  // Terminate if no next cursor, repeated cursor, or max pages reached
                    } else {
                        cursorHistory.add(nextCursor);
                        return fetchPage(1, nextCursor)
                                .delaySubscription(Duration.ofMillis(100)); // Add delay between page requests
                    }
                })
                .flatMap(response -> {
                    // If errors are present, data is usually null or incomplete
                    if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                        log.error("####Iteration " + iteration[0] + " - Error received: " + response.getErrors());
                        // Handle error, but continue processing any valid data
                        return Mono.empty(); // Or you can handle it differently based on your requirements
                    }

                    // Process the items if data is not null
                    return response.getData() != null ? processPage(response) : Flux.empty();
                })
                .retryWhen(retrySpec())
                .onErrorResume(this::handleError);
    }

    private int calculateMaxPages(int totalItems, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Calculate API limit based on rate limit and safety margin
        int maxRequestsPerMinute = 60_000 / TIME_PER_REQUEST_MS; // Convert 60 seconds to milliseconds
        int safeRequests = (API_RATE_LIMIT * (100 - SAFETY_MARGIN_PERCENTAGE)) / 100;
        int apiLimitedMaxPages = Math.min(safeRequests, maxRequestsPerMinute);

        return Math.min(totalPages, apiLimitedMaxPages); // Choose the lesser of the two
    }

    private Mono<QueryResult<PaginatedItems>> fetchPage(int limit, String nextCursor) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("limit", limit);
        variables.put("nextCursor", nextCursor);
        // language=Graphql
        String document = """
                query operatingSystemsWithCursor($limit:Int,$nextCursor:String){
                   operatingSystemsWithCursor(limit: $limit, cursor: $nextCursor) {
                       items {
                           id
                           name
                       }
                       nextCursor
                       totalElements
                   }
                }
                """;

        return graphQlClient.getGraphQLQueryResult(document, "operatingSystemsWithCursor", variables,
                new ParameterizedTypeReference<PaginatedItems>() {
                });
    }

    private Flux<OperatingSystem> processPage(QueryResult<PaginatedItems> response) {
        return Flux.fromIterable(response.getData().getItems())
                .doOnNext(item -> System.out.println("Processing item: " + JsonUtils.toJson(item)))
                .doOnComplete(() -> System.out.println("Completed processing a batch"));
    }

    private RetryBackoffSpec retrySpec() {
        return Retry.backoff(3, Duration.ofSeconds(2))
                .filter(WebClientResponseException.class::isInstance)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new RuntimeException("Max retries reached", retrySignal.failure()));
    }

    private Mono<? extends OperatingSystem> handleError(Throwable throwable) {
        log.error("Error occurred: " + throwable.getMessage());
        return Mono.empty(); // Fallback to an empty response or alternative logic
    }
}

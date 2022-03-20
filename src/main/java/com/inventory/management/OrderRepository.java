package com.inventory.management;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.time.Instant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, String> {

    @Query("SELECT * FROM stock WHERE item_id = :itemId ORDER BY id DESC LIMIT 1")
    Mono<OrderEntity> findByItemId(String itemId);

    @Query("SELECT * FROM stock WHERE created_on >= :fromDate AND created_on <= :toDate")
    Flux<OrderEntity> findByFromToDate(Instant fromDate, Instant toDate);

    @Query("SELECT * FROM stock WHERE created_on = :fromDate")
    Flux<OrderEntity> findByDate(Instant fromDate);
}

package com.inventory.management;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.time.Instant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockRepository extends ReactiveCrudRepository<StockEntity, String> {

    @Query("SELECT * FROM stock WHERE item_id = :itemId ORDER BY id DESC LIMIT 1")
    Mono<StockEntity> findByItemId(String itemId);

    @Query("SELECT * FROM stock WHERE created_on >= :fromDate AND created_on <= :toDate")
    Flux<StockEntity> findByFromToDate(Instant fromDate, Instant toDate);

    @Query("SELECT * FROM stock WHERE created_on = :fromDate")
    Flux<StockEntity> findByDate(Instant fromDate);
}

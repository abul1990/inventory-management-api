package com.inventory.management;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.time.Instant;

import reactor.core.publisher.Flux;

public interface ProcurementRepository extends ReactiveCrudRepository<ProcurementEntity, String> {

    @Query("SELECT * FROM procurement WHERE item_id = :itemId")
    Flux<ProcurementEntity> findByItemId(String itemId);

    @Query("SELECT * FROM procurement WHERE supplier = :supplier AND purchased_on = :date")
    Flux<ProcurementEntity> findBySupplierAndDate(String supplier, Instant date);

    @Query("SELECT * FROM procurement WHERE supplier = :supplier")
    Flux<ProcurementEntity> findBySupplier(String supplier);

    @Query("SELECT * FROM procurement WHERE purchased_on = :date")
    Flux<ProcurementEntity> findByDate(Instant date);

    @Query("SELECT * FROM procurement WHERE purchased_on >= :fromDate AND purchased_on <= :toDate")
    Flux<ProcurementEntity> findByFromToDate(Instant fromDate, Instant toDate);

    @Query("SELECT * FROM procurement WHERE supplier = :supplier AND purchased_on >= :fromDate AND purchased_on <= :toDate")
    Flux<ProcurementEntity> findBySupplierAndFromToDate(String supplier, Instant fromDate, Instant toDate);
}

package com.inventory.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
public class ProcurementController {

    private static final String BY_ITEM = "/items/{item-id}/procurements";
    private static final String BY_PROCUREMENT = "/procurements";

    @Autowired
    ProcurementRepository procurementRepository;

    @Autowired
    CompositeRepository compositeRepository;

    @Autowired
    StockService stockService;

    @PostMapping(path = BY_ITEM, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Procurement>> addProcurement(@PathVariable("item-id") final String itemId, @RequestBody final Mono<Procurement> procurement) {
        return procurement.flatMap(newProcurement -> saveProcurement(itemId, newProcurement, true))
                .delayUntil(procurementEntity -> stockService.saveUpdateStockByItem(Stock.builder()
                        .itemId(procurementEntity.getItemId())
                        .quantity(procurementEntity.getQuantity())
                        .price(procurementEntity.getPrice())
                        .build()))
                .map(this::buildProcurement)
                .map(createdProcurement -> new ResponseEntity<>(createdProcurement, HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @GetMapping(path = BY_ITEM)
    public Mono<ResponseEntity<List<Procurement>>> fetchProcurementByItem(@PathVariable("item-id") final String itemId) {
        return compositeRepository.findProcurementByItemId(itemId)
                .collect(Collectors.toUnmodifiableList())
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(() -> new InventoryException(HttpStatus.NOT_FOUND, "No Procurement available for Item " + itemId)));
    }

    @GetMapping(path = BY_PROCUREMENT)
    public Mono<ResponseEntity<List<Procurement>>> fetchProcurement(@RequestParam @Nullable final String supplier,
                                                                    @RequestParam @Nullable final Instant fromDate,
                                                                    @RequestParam @Nullable final Instant toDate) {
        if (supplier != null && fromDate != null && toDate != null) {
            return procurementRepository.findBySupplierAndFromToDate(supplier, fromDate, toDate)
                    .map(this::buildProcurement)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        } else if (fromDate != null && toDate != null) {
            return procurementRepository.findByFromToDate(fromDate, toDate)
                    .map(this::buildProcurement)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        } else if (supplier != null && fromDate != null) {
            return procurementRepository.findBySupplierAndDate(supplier, fromDate)
                    .map(this::buildProcurement)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        } else if (supplier != null) {
            return procurementRepository.findBySupplier(supplier)
                    .map(this::buildProcurement)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        } else if (fromDate != null) {
            return procurementRepository.findByDate(fromDate)
                    .map(this::buildProcurement)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        }
        return compositeRepository.fetchProcurement()
                .collect(Collectors.toUnmodifiableList())
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = BY_PROCUREMENT + "/{id}")
    public Mono<ResponseEntity<Procurement>> getProcurement(@PathVariable final String id) {
        return compositeRepository.findProcurementById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(() -> new InventoryException(HttpStatus.NOT_FOUND, "No Procurement available for Id " + id)));
    }

    @PatchMapping(path = BY_ITEM + "/{id}")
    public Mono<ResponseEntity<Procurement>> updateProcurement(@PathVariable("item-id") final String itemId,
                                                               @PathVariable final String id, @RequestBody final Mono<Procurement> procurement) {
        return procurement.flatMap(updateProcurement -> saveProcurement(itemId, updateProcurement, false))
                .map(this::buildProcurement)
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @DeleteMapping(path = BY_PROCUREMENT + "/{id}")
    public Mono<ResponseEntity<Void>> deleteProcurement(@PathVariable final String id) {
        return procurementRepository.deleteById(id)
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    private Mono<ProcurementEntity> saveProcurement(String itemId, Procurement procurement, boolean isNew) {
        return procurementRepository.save(ProcurementEntity.builder()
                .id(isNew ? null : procurement.getId())
                .itemId(itemId)
                .quantity(procurement.getQuantity())
                .price(procurement.getPrice())
                .totalPrice(procurement.getQuantity() * procurement.getPrice())
                .supplier(procurement.getSupplier())
                .contactNo(procurement.getContactNo())
                .purchasedOn(procurement.getPurchasedOn())
                .isNew(isNew)
                .build());
    }

    private Procurement buildProcurement(ProcurementEntity procurementEntity) {
        return Procurement.builder()
                .id(procurementEntity.getId())
                .itemId(procurementEntity.getItemId())
                .quantity(procurementEntity.getQuantity())
                .price(procurementEntity.getPrice())
                .totalPrice(procurementEntity.getTotalPrice())
                .supplier(procurementEntity.getSupplier())
                .contactNo(procurementEntity.getContactNo())
                .purchasedOn(procurementEntity.getPurchasedOn())
                .build();
    }
}

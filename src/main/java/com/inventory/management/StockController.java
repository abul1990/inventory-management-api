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
public class StockController {

    private static final String BY_ITEM = "/items/{item-id}/stocks";
    private static final String BY_STOCK = "/stocks";

    @Autowired
    StockRepository stockRepository;

    @Autowired
    CompositeRepository compositeRepository;

    @Autowired
    StockService stockService;

    @PostMapping(path = BY_ITEM, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Stock>> addStock(@PathVariable("item-id") final String itemId, @RequestBody final Mono<Stock> stock) {
        return stock.flatMap(newStock -> stockService.saveUpdateStockByItem(newStock))
                .map(createdStock -> new ResponseEntity<>(createdStock, HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @GetMapping(path = BY_ITEM)
    public Mono<ResponseEntity<Stock>> fetchStockByItem(@PathVariable("item-id") final String itemId) {
        return compositeRepository.findStockByItemId(itemId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new InventoryException(HttpStatus.NOT_FOUND, "No Stock found for Item " + itemId)));
    }

    @GetMapping(path = BY_STOCK)
    public Mono<ResponseEntity<List<Stock>>> fetchStock(@RequestParam @Nullable final Instant fromDate,
                                                        @RequestParam @Nullable final Instant toDate) {
        if (fromDate != null && toDate != null) {
            return stockRepository.findByFromToDate(fromDate, toDate)
                    .map(this::buildStock)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        } else if (fromDate != null) {
            return stockRepository.findByDate(fromDate)
                    .map(this::buildStock)
                    .collect(Collectors.toUnmodifiableList())
                    .map(ResponseEntity::ok);
        }
        return compositeRepository.fetchStock()
                .collect(Collectors.toUnmodifiableList())
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = BY_STOCK + "/{id}")
    public Mono<ResponseEntity<Stock>> getStock(@PathVariable final String id) {
        return compositeRepository.findStockById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new InventoryException(HttpStatus.NOT_FOUND, "No Stock found for id " + id)));
    }

    @PatchMapping(path = BY_ITEM + "/{id}")
    public Mono<ResponseEntity<Stock>> updateStock(@PathVariable("item-id") final String itemId,
                                                   @PathVariable final String id, @RequestBody final Mono<Stock> stock) {
        return stock.flatMap(updateStock -> {
                    updateStock.setId(id);
                    updateStock.setItemId(itemId);
                    return stockService.saveUpdateStockByItem(updateStock);
                })
                .map(ResponseEntity::ok)
                .onErrorResume(throwable -> Mono.error(new InventoryException(HttpStatus.NOT_FOUND, throwable.getMessage())));
    }

    @DeleteMapping(path = BY_STOCK + "/{id}")
    public Mono<ResponseEntity<Void>> deleteStock(@PathVariable final String id) {
        return stockRepository.deleteById(id)
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    private Stock buildStock(StockEntity stockEntity) {
        return Stock.builder()
                .id(stockEntity.getId())
                .itemId(stockEntity.getItemId())
                .quantity(stockEntity.getQuantity())
                .price(stockEntity.getPrice())
                .totalPrice(stockEntity.getTotalPrice())
                .createdOn(stockEntity.getCreatedOn())
                .build();
    }
}

package com.inventory.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockService {
    @Autowired
    StockRepository stockRepository;

    public Mono<Stock> saveUpdateStockByItem(final Stock stock) {
        return stockRepository.findByItemId(stock.getItemId())
                .flatMap(stockEntity -> {
                    stock.setId(stockEntity.getId());
                    stock.setQuantity(stockEntity.getQuantity() + stock.getQuantity());
                    stock.setCreatedOn(Instant.now());
                    return saveUpdateStock(stock, false);
                })
                .switchIfEmpty(Mono.defer(() -> saveUpdateStock(stock, true)))
                .map(stockEntity -> Stock.builder()
                        .id(stockEntity.getId())
                        .itemId(stockEntity.getItemId())
                        .quantity(stockEntity.getQuantity())
                        .price(stockEntity.getPrice())
                        .totalPrice(stockEntity.getTotalPrice())
                        .build());
    }

    public Mono<StockEntity> isStockAvailable(final String itemId, final Integer quantity) {
        return stockRepository.findByItemId(itemId)
                .filter(stockEntity -> stockEntity.getQuantity() >= quantity);
    }

    public Mono<StockEntity> updateStockOnOrder(final Stock stock) {
        return saveUpdateStock(stock, false);
    }

    private Mono<StockEntity> saveUpdateStock(Stock stock, boolean isNew) {
        return stockRepository.save(StockEntity.builder()
                .id(isNew ? null : stock.getId())
                .itemId(stock.getItemId())
                .quantity(stock.getQuantity())
                .price(stock.getPrice())
                .totalPrice(stock.getPrice() * stock.getQuantity())
                .createdOn(stock.getCreatedOn())
                .isNew(isNew)
                .build());
    }
}

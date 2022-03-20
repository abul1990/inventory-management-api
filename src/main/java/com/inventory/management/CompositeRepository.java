package com.inventory.management;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import java.time.Instant;

import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CompositeRepository {

    private final DatabaseClient databaseClient;

    Mono<Stock> findStockById(String id) {
        return databaseClient.sql("SELECT stock.*, item.name as item_name FROM stock " +
                        "INNER JOIN item ON stock.item_id = item.id " +
                        "WHERE stock.id = :id")
                .bind("id", id)
                .map(this::mapToStock)
                .one();
    }

    Mono<Stock> findStockByItemId(String itemId) {
        return databaseClient.sql("SELECT stock.*, item.name as item_name FROM stock " +
                        "INNER JOIN item ON stock.item_id = item.id " +
                        "WHERE item_id = :itemId")
                .bind("itemId", itemId)
                .map(this::mapToStock)
                .one();
    }

    Flux<Stock> fetchStock() {
        return databaseClient.sql("SELECT stock.*, item.name as item_name FROM stock " +
                        "INNER JOIN item ON stock.item_id = item.id")
                .map(this::mapToStock)
                .all();
    }

    Mono<Procurement> findProcurementById(String id) {
        return databaseClient.sql("SELECT procurement.*, item.name as item_name FROM procurement " +
                        "INNER JOIN item ON stock.item_id = item.id " +
                        "WHERE stock.id = :id")
                .bind("id", id)
                .map(this::mapToProcurement)
                .one();
    }

    Flux<Procurement> findProcurementByItemId(String itemId) {
        return databaseClient.sql("SELECT procurement.*, item.name as item_name FROM procurement " +
                        "INNER JOIN item ON procurement.item_id = item.id " +
                        "WHERE item_id = :itemId")
                .bind("itemId", itemId)
                .map(this::mapToProcurement)
                .all();
    }

    Flux<Procurement> fetchProcurement() {
        return databaseClient.sql("SELECT procurement.*, item.name as item_name FROM procurement " +
                        "INNER JOIN item ON procurement.item_id = item.id ")
                .map(this::mapToProcurement)
                .all();
    }

    Flux<Order> fetchOrder() {
        return databaseClient.sql("SELECT orders.*, customer.name as customer_name, item.name as item_name FROM orders " +
                        "INNER JOIN customer ON customer.id = orders.customer_id " +
                        "INNER JOIN item ON item.id = orders.item_id ")
                .map(this::mapToOrder)
                .all();
    }

    private Stock mapToStock(Row row) {
        return Stock.builder()
                .id(row.get("id", String.class))
                .itemId(row.get("item_id", String.class))
                .itemName(row.get("item_name", String.class))
                .quantity(row.get("quantity", Integer.class))
                .price(row.get("price", Double.class))
                .totalPrice(row.get("total_price", Double.class))
                .createdOn(row.get("created_on", Instant.class))
                .build();
    }

    private Procurement mapToProcurement(Row row) {
        return Procurement.builder()
                .id(row.get("id", String.class))
                .itemId(row.get("item_id", String.class))
                .itemName(row.get("item_name", String.class))
                .quantity(row.get("quantity", Integer.class))
                .price(row.get("price", Double.class))
                .totalPrice(row.get("total_price", Double.class))
                .supplier(row.get("supplier", String.class))
                .purchasedOn(row.get("purchased_on", Instant.class))
                .build();
    }

    private Order mapToOrder(Row row) {
        return Order.builder()
                .id(row.get("id", String.class))
                .customerId(row.get("customer_id", String.class))
                .customerName(row.get("customer_name", String.class))
                .itemId(row.get("item_id", String.class))
                .itemName(row.get("item_name", String.class))
                .quantity(row.get("quantity", Integer.class))
                .price(row.get("price", Double.class))
                .discount(row.get("discount", Double.class))
                .netPrice(row.get("net_price", Double.class))
                .totalPrice(row.get("total_price", Double.class))
                .createdOn(row.get("created_on", Instant.class))
                .build();
    }
}

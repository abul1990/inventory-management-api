package com.inventory.management;

import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("MissingOverride")
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("stock")
@Builder
public class StockEntity implements Persistable<String> {
    @Id
    String id;
    String itemId;
    Integer quantity;
    Double price;
    Double totalPrice;
    Instant createdOn;

    @Transient
    transient private boolean isNew;

    @PersistenceConstructor
    public StockEntity(String id, String itemId, Integer quantity, Double price, Double totalPrice, Instant createdOn) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.createdOn = createdOn;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}

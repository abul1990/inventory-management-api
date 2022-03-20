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
@Table("orders")
@Builder
public class OrderEntity implements Persistable<String> {
    @Id
    String id;
    String customerId;
    String itemId;
    Integer quantity;
    Double price;
    Double discount;
    Double netPrice;
    Double totalPrice;
    Instant createdOn;

    @Transient
    transient private boolean isNew;

    @PersistenceConstructor
    public OrderEntity(String id, String customerId, String itemId, Integer quantity, Double price, Double discount, Double netPrice, Double totalPrice, Instant createdOn) {
        this.id = id;
        this.customerId = customerId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.netPrice = netPrice;
        this.totalPrice = totalPrice;
        this.createdOn = createdOn;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}

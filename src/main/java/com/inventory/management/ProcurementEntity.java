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
@Table("procurement")
@Builder
public class ProcurementEntity implements Persistable<String> {
    @Id
    String id;
    String itemId;
    Integer quantity;
    Double price;
    Double totalPrice;
    String supplier;
    Integer contactNo;
    Instant purchasedOn;

    @Transient
    transient private boolean isNew;

    @PersistenceConstructor
    public ProcurementEntity(String id, String itemId, Integer quantity, Double price, Double totalPrice, String supplier, Integer contactNo, Instant purchasedOn) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.supplier = supplier;
        this.contactNo = contactNo;
        this.purchasedOn = purchasedOn;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}

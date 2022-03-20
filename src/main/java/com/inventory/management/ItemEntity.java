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
@Table("item")
@Builder
public class ItemEntity implements Persistable<String> {
    @Id
    String id;
    String name;
    Double discountRetail;
    Double discountWholesale;
    Instant createdOn;

    @Transient
    transient private boolean isNew;

    @PersistenceConstructor
    public ItemEntity(String id, String name, Double discountRetail, Double discountWholesale, Instant createdOn) {
        this.id = id;
        this.name = name;
        this.discountRetail = discountRetail;
        this.discountWholesale = discountWholesale;
        this.createdOn = createdOn;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}

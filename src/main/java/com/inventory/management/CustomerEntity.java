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
@Table("customer")
@Builder
public class CustomerEntity implements Persistable<String> {
    @Id
    String id;
    String name;
    InventoryConstants.CustomerType type;
    String address;
    Integer contactNo;
    Instant createdOn;

    @Transient
    transient private boolean isNew;

    @PersistenceConstructor
    public CustomerEntity(String id, String name, InventoryConstants.CustomerType type, String address, Integer contactNo, Instant createdOn) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.contactNo = contactNo;
        this.createdOn = createdOn;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}

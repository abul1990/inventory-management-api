package com.inventory.management;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {
    String id;
    String name;
    InventoryConstants.CustomerType type;
    String address;
    Integer contactNo;
    Instant createdOn;
}

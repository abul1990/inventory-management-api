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
public class Stock {
    String id;
    String itemId;
    String itemName;
    Integer quantity;
    Double price;
    Double totalPrice;
    Instant createdOn;
}

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
public class Procurement {
    String id;
    String itemId;
    String itemName;
    Integer quantity;
    Double price;
    Double totalPrice;
    String supplier;
    Integer contactNo;
    Instant purchasedOn;
}

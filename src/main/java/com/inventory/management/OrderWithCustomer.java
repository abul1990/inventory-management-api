package com.inventory.management;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderWithCustomer {
    Customer customer;
    List<Order> orders;
}

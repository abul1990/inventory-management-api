package com.inventory.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CompositeRepository compositeRepository;

    @Autowired
    StockService stockService;

    @Autowired
    CustomerService customerService;

    /*@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Order>>> placeOrder(@RequestBody final Flux<Order> order) {
        return orderAndUpdateStock(order);
    }*/

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Order>>> placeOrderWithCustomer(@RequestBody final Mono<OrderWithCustomer> orderWithCustomer) {
        return orderWithCustomer
                .flatMap(newOrder -> (newOrder.getCustomer().getId() != null ?
                        customerService.isCustomerExists(newOrder.getCustomer().getId())
                                .switchIfEmpty(Mono.defer(() -> customerService.addCustomer(newOrder.getCustomer()))) :
                        customerService.addCustomer(newOrder.getCustomer()))
                        .flatMap(customer -> orderAndUpdateStock(Mono.fromCallable(newOrder::getOrders).flatMapMany(Flux::fromIterable), customer.getId())));
    }

    @GetMapping
    public Mono<ResponseEntity<List<Order>>> fetchOrder() {
        return compositeRepository.fetchOrder()
                .collect(Collectors.toUnmodifiableList())
                .map(ResponseEntity::ok);
    }

    private Mono<ResponseEntity<List<Order>>> orderAndUpdateStock(Flux<Order> order, String customerId) {
        return order.flatMap(newOrder -> stockService.isStockAvailable(newOrder.getItemId(), newOrder.getQuantity())
                        .flatMap(stockEntity -> orderRepository.save(OrderEntity.builder()
                                        .customerId(customerId)
                                        .itemId(newOrder.getItemId())
                                        .quantity(newOrder.getQuantity())
                                        .price(newOrder.getPrice())
                                        .discount(newOrder.getDiscount())
                                        .netPrice(newOrder.getNetPrice())
                                        .totalPrice(newOrder.getQuantity() * newOrder.getNetPrice())
                                        .createdOn(newOrder.getCreatedOn())
                                        .isNew(true)
                                        .build())
                                .delayUntil(__ -> stockService.updateStockOnOrder(Stock.builder()
                                        .id(stockEntity.getId())
                                        .itemId(stockEntity.getItemId())
                                        .price(stockEntity.getPrice())
                                        .quantity(stockEntity.getQuantity() - newOrder.getQuantity())
                                        .totalPrice((stockEntity.getQuantity() - newOrder.getQuantity()) * stockEntity.getPrice())
                                        .createdOn(stockEntity.getCreatedOn())
                                        .build()))))
                .map(orderEntity -> Order.builder()
                        .customerId(orderEntity.getCustomerId())
                        .itemId(orderEntity.getItemId())
                        .quantity(orderEntity.getQuantity())
                        .price(orderEntity.getPrice())
                        .discount(orderEntity.getDiscount())
                        .netPrice(orderEntity.getNetPrice())
                        .totalPrice(orderEntity.getTotalPrice())
                        .createdOn(orderEntity.getCreatedOn())
                        .build())
                .collect(Collectors.toUnmodifiableList())
                .map(createdOrder -> new ResponseEntity<>(createdOrder, HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }
}

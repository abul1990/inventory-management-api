package com.inventory.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping(path = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Customer>> addCustomer(@RequestBody final Mono<Customer> customer) {
        return customer.flatMap(newCustomer -> customerService.addCustomer(newCustomer))
                .map(createdCustomer -> new ResponseEntity<>(createdCustomer, HttpStatus.CREATED))
                .doOnError(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @GetMapping
    public Mono<ResponseEntity<List<Customer>>> fetchCustomer() {
        return customerRepository.findAll()
                .map(this::buildCustomer)
                .collect(Collectors.toUnmodifiableList())
                .map(ResponseEntity::ok)
                .doOnError(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @GetMapping(path = "{id}")
    public Mono<ResponseEntity<Customer>> getCustomer(@PathVariable final String id) {
        return customerRepository.findById(id)
                .map(this::buildCustomer)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new InventoryException(HttpStatus.NOT_FOUND, "No Item found for id " + id)));
    }

    @PatchMapping(path = "{id}")
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable final String id, @RequestBody final Mono<Customer> customer) {
        return customer.flatMap(updateCustomer -> {
                    updateCustomer.setId(id);
                    return customerService.addCustomer(updateCustomer);
                })
                .map(ResponseEntity::ok)
                .doOnError(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @DeleteMapping(path = "{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable final String id) {
        return customerRepository.deleteById(id)
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    private Customer buildCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .id(customerEntity.getId())
                .name(customerEntity.getName())
                .type(customerEntity.getType())
                .contactNo(customerEntity.getContactNo())
                .address(customerEntity.getAddress())
                .createdOn(customerEntity.getCreatedOn())
                .build();
    }
}

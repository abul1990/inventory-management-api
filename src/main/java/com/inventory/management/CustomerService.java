package com.inventory.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    public Mono<Customer> addCustomer(final Customer customer) {
        return customerRepository.findById(customer.getId() != null ? customer.getId() : "")
                .flatMap(customerEntity -> saveUpdateCustomer(customer, false))
                .switchIfEmpty(Mono.defer(() -> saveUpdateCustomer(customer, true)))
                .map(customerEntity -> Customer.builder()
                        .id(customerEntity.getId())
                        .type(customerEntity.getType())
                        .contactNo(customerEntity.getContactNo())
                        .address(customerEntity.getAddress())
                        .createdOn(customerEntity.getCreatedOn())
                        .build());
    }

    public Mono<Customer> isCustomerExists(final String id) {
        return customerRepository.findById(id)
                .map(customerEntity -> Customer.builder()
                        .id(customerEntity.getId())
                        .build());
    }

    private Mono<CustomerEntity> saveUpdateCustomer(Customer customer, boolean isNew) {
        return customerRepository.save(CustomerEntity.builder()
                .id(isNew ? null : customer.getId())
                .name(customer.getName())
                .type(customer.getType())
                .contactNo(customer.getContactNo())
                .address(customer.getAddress())
                .createdOn(customer.getCreatedOn())
                .isNew(isNew)
                .build());
    }
}

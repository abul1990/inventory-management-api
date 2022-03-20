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
@RequestMapping(path = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin("*")
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Item>> addItem(@RequestBody final Mono<Item> item) {
        return item.flatMap(newItem -> itemRepository.save(ItemEntity.builder()
                        .name(newItem.getName())
                        .discountRetail(newItem.getDiscountRetail())
                        .discountWholesale(newItem.getDiscountWholesale())
                        .createdOn(newItem.getCreatedOn())
                        .isNew(true)
                        .build()))
                .map(itemEntity -> buildItem(itemEntity))
                .map(item1 -> new ResponseEntity<>(item1, HttpStatus.CREATED))
                .doOnError(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @GetMapping
    public Mono<ResponseEntity<List<Item>>> fetchItem() {
        return itemRepository.findAll()
                .map(itemEntity -> buildItem(itemEntity))
                .collect(Collectors.toUnmodifiableList())
                .map(ResponseEntity::ok)
                .doOnError(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @GetMapping(path = "{id}")
    public Mono<ResponseEntity<Item>> getItem(@PathVariable final String id) {
        return itemRepository.findById(id)
                .map(itemEntity -> buildItem(itemEntity))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new InventoryException(HttpStatus.NOT_FOUND, "No Item found for id " + id)));
    }

    @PatchMapping(path = "{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable final String id, @RequestBody final Mono<Item> item) {
        return item.flatMap(updateItem -> itemRepository.save(ItemEntity.builder()
                        .id(id)
                        .name(updateItem.getName())
                        .build()))
                .map(itemEntity -> buildItem(itemEntity))
                .map(ResponseEntity::ok)
                .doOnError(throwable -> Mono.error(new InventoryException(HttpStatus.BAD_REQUEST, throwable.getMessage())));
    }

    @DeleteMapping(path = "{id}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable final String id) {
        return itemRepository.deleteById(id)
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    private Item buildItem(ItemEntity itemEntity) {
        return Item.builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .discountRetail(itemEntity.getDiscountRetail())
                .discountWholesale(itemEntity.getDiscountWholesale())
                .createdOn(itemEntity.getCreatedOn())
                .build();
    }
}

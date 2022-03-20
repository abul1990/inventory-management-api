package com.inventory.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/poc", produces = MediaType.APPLICATION_JSON_VALUE)
public class PoCController {
}

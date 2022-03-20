package com.inventory.management;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class InventoryException extends HttpClientErrorException {
    public InventoryException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}

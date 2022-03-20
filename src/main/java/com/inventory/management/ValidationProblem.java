package com.inventory.management;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationProblem {
    Instant timestamp;
    String path;
    Integer status;
    String error;
    String message;
}

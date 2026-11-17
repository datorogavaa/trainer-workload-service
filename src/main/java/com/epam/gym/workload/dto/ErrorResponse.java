package com.epam.gym.workload.dto;

public record ErrorResponse(String transactionId, int status, String message) {
}

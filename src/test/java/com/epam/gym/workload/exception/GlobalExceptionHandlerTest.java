package com.epam.gym.workload.exception;

import com.epam.gym.workload.dto.ErrorResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void clearTransactionId() {
        org.slf4j.MDC.clear();
    }

    @Test
    void notFoundReturnsNotFoundResponseWithTransactionId() {
        org.slf4j.MDC.put("transactionId", "tx-1");

        var response = handler.notFound(new NotFoundException("Trainer not found"));

        assertEquals(404, response.getStatusCode().value());
        assertEquals(new ErrorResponse("tx-1", 404, "Trainer not found"), response.getBody());
    }

    @Test
    void unreadableReturnsBadRequest() {
        var response = handler.unreadable(new HttpMessageNotReadableException("invalid", (HttpInputMessage) null));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Malformed request body", response.getBody().message());
    }

    @Test
    void genericReturnsInternalServerError() {
        var response = handler.generic(new IllegalStateException("failure"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody().message());
    }
}

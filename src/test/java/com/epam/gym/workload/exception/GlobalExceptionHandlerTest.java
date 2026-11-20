package com.epam.gym.workload.exception;

import com.epam.gym.workload.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.mock.http.MockHttpInputMessage;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundReturns404() {
        ResponseEntity<ErrorResponse> response =
                handler.notFound(new NotFoundException("Missing trainer"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Missing trainer", response.getBody().message());
    }

    @Test
    void invalidRequestReturnsFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "workloadRequest");
        bindingResult.addError(new FieldError(
                "workloadRequest", "trainerUsername", "must not be blank"));
        Method method = TestController.class.getMethod("update", Object.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.invalid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("trainerUsername: must not be blank", response.getBody().message());
    }

    @Test
    void unreadableRequestReturns400() throws Exception {
        HttpInputMessage input = new MockHttpInputMessage("not-json".getBytes());
        Method method = TestController.class.getMethod("update", Object.class);
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("Malformed", input);

        ResponseEntity<ErrorResponse> response = handler.unreadable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed request body", response.getBody().message());
    }

    @Test
    void genericExceptionReturns500() {
        ResponseEntity<ErrorResponse> response =
                handler.generic(new IllegalStateException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().message());
    }

    private static class TestController {
        public void update(Object request) {
        }
    }
}

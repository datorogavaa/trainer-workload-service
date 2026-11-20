package com.epam.gym.workload.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionLoggingFilterTest {

    private final TransactionLoggingFilter filter = new TransactionLoggingFilter();

    @AfterEach
    void clearMdc() {
        org.slf4j.MDC.clear();
    }

    @Test
    void generatesTransactionIdAndCopiesResponseBody() throws Exception {
        var request = new MockHttpServletRequest("POST", "/api/trainers/workloads");
        request.setContent("{\"trainerUsername\":\"john.doe\"}".getBytes(StandardCharsets.UTF_8));
        var response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            res.getWriter().write("accepted");
            ((jakarta.servlet.http.HttpServletResponse) res).setStatus(200);
        };

        filter.doFilter(request, response, chain);

        assertNotNull(response.getHeader(TransactionLoggingFilter.TRANSACTION_HEADER));
        assertEquals("accepted", response.getContentAsString());
        assertNull(org.slf4j.MDC.get(TransactionLoggingFilter.MDC_KEY));
    }

    @Test
    void preservesIncomingTransactionIdAndCleansMdcWhenChainFails() {
        var request = new MockHttpServletRequest("GET", "/api/trainers/john.doe/workloads");
        request.addHeader(TransactionLoggingFilter.TRANSACTION_HEADER, "tx-42");
        var response = new MockHttpServletResponse();
        var exception = new IllegalStateException("failure");
        FilterChain chain = (req, res) -> {
            throw exception;
        };

        var thrown = assertThrows(IllegalStateException.class,
                () -> filter.doFilter(request, response, chain));

        assertSame(exception, thrown);
        assertEquals("tx-42", response.getHeader(TransactionLoggingFilter.TRANSACTION_HEADER));
        assertNull(org.slf4j.MDC.get(TransactionLoggingFilter.MDC_KEY));
    }
}

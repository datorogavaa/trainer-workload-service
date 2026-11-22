package com.epam.gym.workload.messaging;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.service.WorkloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class WorkloadMessageListenerTest {

    private static final String TRANSACTION_ID_KEY = "transactionId";

    private final WorkloadService service = mock(WorkloadService.class);
    private final WorkloadMessageListener listener = new WorkloadMessageListener(service);

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void receiveDelegatesValidRequestToServiceAndSetsMdcTransactionId() {
        WorkloadRequest valid = validRequest();
        String txId = "tx-12345";
        AtomicReference<String> mdcDuringProcess = new AtomicReference<>();

        doAnswer(invocation -> {
            mdcDuringProcess.set(MDC.get(TRANSACTION_ID_KEY));
            return null;
        }).when(service).process(valid);

        listener.receive(valid, txId);

        verify(service).process(valid);
        assertEquals(txId, mdcDuringProcess.get());
        assertNull(MDC.get(TRANSACTION_ID_KEY));
    }

    @Test
    void receiveGeneratesFallbackTransactionIdWhenHeaderMissingAndClearsMdc() {
        WorkloadRequest valid = validRequest();
        AtomicReference<String> mdcDuringProcess = new AtomicReference<>();

        doAnswer(invocation -> {
            mdcDuringProcess.set(MDC.get(TRANSACTION_ID_KEY));
            return null;
        }).when(service).process(valid);

        listener.receive(valid, null);

        verify(service).process(valid);
        org.junit.jupiter.api.Assertions.assertNotNull(mdcDuringProcess.get());
        org.junit.jupiter.api.Assertions.assertFalse(mdcDuringProcess.get().isBlank());
        assertNull(MDC.get(TRANSACTION_ID_KEY));
    }

    @Test
    void receiveRejectsNullRequestAndClearsMdc() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> listener.receive(null, "tx-null"));

        assertEquals("Invalid workload message: required fields are missing", ex.getMessage());
        verifyNoInteractions(service);
        assertNull(MDC.get(TRANSACTION_ID_KEY));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void receiveRejectsInvalidRequestAndClearsMdc(WorkloadRequest invalid) {
        assertThrows(IllegalArgumentException.class, () -> listener.receive(invalid, "tx-invalid"));

        verifyNoInteractions(service);
        assertNull(MDC.get(TRANSACTION_ID_KEY));
    }

    static Stream<WorkloadRequest> invalidRequests() {
        LocalDate date = LocalDate.of(2026, 1, 15);
        return Stream.of(
                request(null, "John", "Doe", true, date, 60, ActionType.ADD),
                request("", "John", "Doe", true, date, 60, ActionType.ADD),
                request("   ", "John", "Doe", true, date, 60, ActionType.ADD),
                request("john.doe", null, "Doe", true, date, 60, ActionType.ADD),
                request("john.doe", " ", "Doe", true, date, 60, ActionType.ADD),
                request("john.doe", "John", null, true, date, 60, ActionType.ADD),
                request("john.doe", "John", " ", true, date, 60, ActionType.ADD),
                request("john.doe", "John", "Doe", null, date, 60, ActionType.ADD),
                request("john.doe", "John", "Doe", true, null, 60, ActionType.ADD),
                request("john.doe", "John", "Doe", true, date, null, ActionType.ADD),
                request("john.doe", "John", "Doe", true, date, 0, ActionType.ADD),
                request("john.doe", "John", "Doe", true, date, -5, ActionType.ADD),
                request("john.doe", "John", "Doe", true, date, 60, null)
        );
    }

    private static WorkloadRequest validRequest() {
        return request("john.doe", "John", "Doe", true, LocalDate.of(2026, 1, 15), 60, ActionType.ADD);
    }

    // Single place to adjust if the WorkloadRequest constructor differs
    private static WorkloadRequest request(String username, String firstName, String lastName,
                                           Boolean active, LocalDate date, Integer duration,
                                           ActionType action) {
        return new WorkloadRequest(username, firstName, lastName, active, date, duration, action);
    }
}
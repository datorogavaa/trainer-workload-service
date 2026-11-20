package com.epam.gym.workload.messaging;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class WorkloadMessageListenerTest {

    private final WorkloadService service = mock(WorkloadService.class);
    private final WorkloadMessageListener listener = new WorkloadMessageListener(service);

    @Test
    void receiveDelegatesValidRequestToService() {
        WorkloadRequest valid = validRequest();

        listener.receive(valid);

        verify(service).process(valid);
    }

    @Test
    void receiveRejectsNullRequest() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> listener.receive(null));

        assertEquals("Invalid workload message: required fields are missing", ex.getMessage());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void receiveRejectsInvalidRequest(WorkloadRequest invalid) {
        assertThrows(IllegalArgumentException.class, () -> listener.receive(invalid));

        verifyNoInteractions(service);
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
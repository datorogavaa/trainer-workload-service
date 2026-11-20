package com.epam.gym.workload.controller;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.MonthlyHoursResponse;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.model.TrainerWorkload;
import com.epam.gym.workload.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkloadRestControllerTest {

    private final WorkloadService service = mock(WorkloadService.class);
    private final WorkloadRestController controller = new WorkloadRestController(service);

    @Test
    void updateWorkloadDelegatesAndReturnsOk() {
        WorkloadRequest request = new WorkloadRequest(
                "john.doe", "John", "Doe", true, LocalDate.of(2026, 1, 15), 60, ActionType.ADD);

        ResponseEntity<Void> response = controller.updateWorkload(request);

        verify(service).process(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateWorkloadPropagatesServiceException() {
        WorkloadRequest request = new WorkloadRequest(
                "john.doe", "John", "Doe", true, LocalDate.of(2026, 1, 15), 60, ActionType.ADD);
        doThrow(new IllegalStateException("boom")).when(service).process(request);

        assertThrows(IllegalStateException.class, () -> controller.updateWorkload(request));
    }

    @Test
    void getMonthlyHoursDelegatesToService() {
        var expected = new MonthlyHoursResponse("john.doe", 2026, 1, 60);
        when(service.getMonthlyHours("john.doe", 2026, 1)).thenReturn(expected);

        assertSame(expected, controller.getMonthlyHours("john.doe", 2026, 1));
    }

    @Test
    void getMonthlyHoursByPathDelegatesToService() {
        var expected = new MonthlyHoursResponse("john.doe", 2026, 1, 60);
        when(service.getMonthlyHours("john.doe", 2026, 1)).thenReturn(expected);

        assertSame(expected, controller.getMonthlyHoursByPath("john.doe", 2026, 1));
    }

    @Test
    void getWorkloadDelegatesToService() {
        var expected = new TrainerWorkload("john.doe");
        when(service.getWorkload("john.doe")).thenReturn(expected);

        assertSame(expected, controller.getWorkload("john.doe"));
    }
}
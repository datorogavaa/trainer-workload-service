package com.epam.gym.workload.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoTest {

    @Test
    void workloadRequestExposesAllFields() {
        var request = new WorkloadRequest("john.doe", "John", "Doe", true,
                LocalDate.of(2026, 1, 2), 60, ActionType.ADD);

        assertEquals("john.doe", request.trainerUsername());
        assertEquals("John", request.trainerFirstName());
        assertEquals("Doe", request.trainerLastName());
        assertEquals(true, request.isActive());
        assertEquals(LocalDate.of(2026, 1, 2), request.trainingDate());
        assertEquals(60, request.trainingDuration());
        assertEquals(ActionType.ADD, request.actionType());
    }

    @Test
    void responseDtosExposeTheirValues() {
        assertEquals(new ErrorResponse("tx-1", 400, "bad"),
                new ErrorResponse("tx-1", 400, "bad"));
        assertEquals(new MonthlyHoursResponse("john.doe", 2026, 1, 60),
                new MonthlyHoursResponse("john.doe", 2026, 1, 60));
    }
}

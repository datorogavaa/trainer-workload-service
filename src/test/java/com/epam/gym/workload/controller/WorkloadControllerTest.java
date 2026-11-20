package com.epam.gym.workload.controller;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.MonthlyHoursResponse;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.model.TrainerWorkload;
import com.epam.gym.workload.service.WorkloadService;
import com.epam.gym.workload.repository.InMemoryWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkloadControllerTest {

    private final WorkloadService service = new WorkloadService(new InMemoryWorkloadRepository());
    private final WorkloadController controller = new WorkloadController(service);

    @Test
    void updateWorkloadProcessesRequestAndReturnsOk() {
        WorkloadRequest request = new WorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 12, 3), 60, ActionType.ADD);

        assertEquals(HttpStatus.OK, controller.updateWorkload(request).getStatusCode());
        assertEquals(60, controller.getMonthlyHours("john.doe", 2026, 12).trainingSummaryDuration());
    }

    @Test
    void getMonthlyHoursDelegatesToService() {
        service.process(new WorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 12, 3), 60, ActionType.ADD));

        MonthlyHoursResponse actual = controller.getMonthlyHours("john.doe", 2026, 12);

        assertEquals("john.doe", actual.trainerUsername());
        assertEquals(2026, actual.year());
        assertEquals(12, actual.month());
        assertEquals(60, actual.trainingSummaryDuration());
    }

    @Test
    void getMonthlyHoursByQueryDelegatesToService() {
        service.process(new WorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 12, 3), 60, ActionType.ADD));

        MonthlyHoursResponse actual = controller.getMonthlyHoursByQuery("john.doe", 2026, 12);

        assertEquals(60, actual.trainingSummaryDuration());
    }

    @Test
    void getWorkloadDelegatesToService() {
        service.process(new WorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 12, 3), 60, ActionType.ADD));

        TrainerWorkload actual = controller.getWorkload("john.doe");
        assertEquals("john.doe", actual.getTrainerUsername());
        assertEquals("John", actual.getTrainerFirstName());
        assertEquals("Doe", actual.getTrainerLastName());
    }
}

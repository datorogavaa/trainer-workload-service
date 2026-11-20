package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.exception.NotFoundException;
import com.epam.gym.workload.repository.InMemoryWorkloadRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadServiceTest {

    private final WorkloadService service = new WorkloadService(new InMemoryWorkloadRepository());

    private WorkloadRequest req(LocalDate d, int dur, ActionType a) {
        return new WorkloadRequest("john.doe", "John", "Doe", true, d, dur, a);
    }

    @Test
    void addAccumulatesPerMonth() {
        service.process(req(LocalDate.of(2026, 12, 3), 60, ActionType.ADD));
        service.process(req(LocalDate.of(2026, 12, 20), 30, ActionType.ADD));
        service.process(req(LocalDate.of(2027, 1, 5), 45, ActionType.ADD));
        assertEquals(90, service.getMonthlyHours("john.doe", 2026, 12).trainingSummaryDuration());
        assertEquals(45, service.getMonthlyHours("john.doe", 2027, 1).trainingSummaryDuration());
    }

    @Test
    void deleteSubtractsAndNeverGoesNegative() {
        service.process(req(LocalDate.of(2026, 12, 3), 60, ActionType.ADD));
        service.process(req(LocalDate.of(2026, 12, 3), 60, ActionType.DELETE));
        service.process(req(LocalDate.of(2026, 12, 3), 60, ActionType.DELETE));
        assertEquals(0, service.getMonthlyHours("john.doe", 2026, 12).trainingSummaryDuration());
    }

    @Test
    void unknownTrainerIsNotFound() {
        assertThrows(NotFoundException.class, () -> service.getMonthlyHours("nobody", 2026, 1));
    }

    @Test
    void processUpdatesTrainerDetails() {
        service.process(new WorkloadRequest("john.doe", "Jane", "Smith", false,
                LocalDate.of(2026, 2, 1), 30, ActionType.ADD));

        var workload = service.getWorkload("john.doe");
        assertEquals("john.doe", workload.getTrainerUsername());
        assertEquals("Jane", workload.getTrainerFirstName());
        assertEquals("Smith", workload.getTrainerLastName());
        assertFalse(workload.isTrainerStatus());
    }

    @Test
    void deleteWithoutPreviousAdditionCreatesZeroSummary() {
        service.process(req(LocalDate.of(2026, 2, 1), 30, ActionType.DELETE));

        assertEquals(0, service.getMonthlyHours("john.doe", 2026, 2).trainingSummaryDuration());
    }

    @Test
    void getWorkloadReturnsStoredTrainer() {
        service.process(req(LocalDate.of(2026, 2, 1), 30, ActionType.ADD));

        assertSame(service.getWorkload("john.doe"), service.getWorkload("john.doe"));
    }
}

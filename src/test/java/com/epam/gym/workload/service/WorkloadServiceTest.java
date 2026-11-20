package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.exception.NotFoundException;
import com.epam.gym.workload.repository.InMemoryWorkloadRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

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
        assertTrue(service.getWorkload("john.doe").getYears().isEmpty());
    }

    @Test
    void deleteForMissingMonthDoesNotCreateZeroMonth() {
        service.process(req(LocalDate.of(2026, 12, 3), 60, ActionType.ADD));
        service.process(req(LocalDate.of(2027, 1, 5), 30, ActionType.DELETE));

        assertEquals(60, service.getMonthlyHours("john.doe", 2026, 12).trainingSummaryDuration());
        assertEquals(0, service.getMonthlyHours("john.doe", 2027, 1).trainingSummaryDuration());
    }

    @Test
    void unknownTrainerIsNotFound() {
        assertThrows(NotFoundException.class, () -> service.getMonthlyHours("nobody", 2026, 1));
    }

    @Test
    void deleteSubtractsOnlyRequestedDurationAndKeepsMonth() {
        service.process(req(LocalDate.of(2026, 12, 3), 60, ActionType.ADD));
        service.process(req(LocalDate.of(2026, 12, 20), 25, ActionType.DELETE));

        assertEquals(35, service.getMonthlyHours("john.doe", 2026, 12).trainingSummaryDuration());
        assertEquals(1, service.getWorkload("john.doe").getYears().size());
        assertEquals(1, service.getWorkload("john.doe").getYears().get(0).getMonths().size());
    }

    @Test
    void deletingMoreThanMonthlyTotalRemovesMonthAndYear() {
        service.process(req(LocalDate.of(2026, 12, 3), 10, ActionType.ADD));
        service.process(req(LocalDate.of(2026, 12, 20), 50, ActionType.DELETE));

        assertTrue(service.getWorkload("john.doe").getYears().isEmpty());
    }

    @Test
    void processingUpdatesTrainerProfile() {
        service.process(new WorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 12, 3), 60, ActionType.ADD));
        service.process(new WorkloadRequest(
                "john.doe", "Jonathan", "Smith", false,
                LocalDate.of(2026, 12, 20), 30, ActionType.ADD));

        var workload = service.getWorkload("john.doe");
        assertEquals("Jonathan", workload.getTrainerFirstName());
        assertEquals("Smith", workload.getTrainerLastName());
        assertFalse(workload.isTrainerStatus());
    }

    @Test
    void separateMonthsRemainIndependent() {
        service.process(req(LocalDate.of(2026, 10, 1), 20, ActionType.ADD));
        service.process(req(LocalDate.of(2026, 11, 1), 40, ActionType.ADD));
        service.process(req(LocalDate.of(2026, 10, 15), 20, ActionType.DELETE));

        var workload = service.getWorkload("john.doe");
        var year = workload.getYears().get(0);
        assertEquals(1, year.getMonths().size());
        assertEquals(Month.NOVEMBER, year.getMonths().get(0).getMonth());
        assertEquals(0, service.getMonthlyHours("john.doe", 2026, 10).trainingSummaryDuration());
        assertEquals(40, service.getMonthlyHours("john.doe", 2026, 11).trainingSummaryDuration());
    }
}

package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.MonthlyHoursResponse;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.model.MonthSummary;
import com.epam.gym.workload.model.TrainerWorkload;
import com.epam.gym.workload.model.YearSummary;
import com.epam.gym.workload.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkloadServiceTest {

    private final TrainerRepository repository = mock(TrainerRepository.class);
    private final WorkloadService service = new WorkloadService(repository);

    @Test
    void processAddCreatesWorkloadAndMonthlySummaryWhenTrainerIsNew() {
        WorkloadRequest request = request(ActionType.ADD, LocalDate.of(2026, 1, 15), 60);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(null);

        service.process(request);

        ArgumentCaptor<TrainerWorkload> saved = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(saved.capture());
        TrainerWorkload workload = saved.getValue();
        assertEquals("john.doe", workload.getTrainerUsername());
        assertEquals("John", workload.getTrainerFirstName());
        assertEquals("Doe", workload.getTrainerLastName());
        assertTrue(workload.isTrainerStatus());
        assertEquals(1, workload.getYears().size());
        assertEquals(2026, workload.getYears().get(0).getYear());
        assertEquals(Month.JANUARY, workload.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(60, workload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void processAddAccumulatesDurationInExistingMonth() {
        TrainerWorkload workload = workloadWithSummary(2026, Month.JANUARY, 60);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(workload);

        service.process(request(ActionType.ADD, LocalDate.of(2026, 1, 20), 30));

        assertEquals(90, workload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
        verify(repository).save(workload);
    }

    @Test
    void processDeleteSubtractsDurationFromExistingMonth() {
        TrainerWorkload workload = workloadWithSummary(2026, Month.JANUARY, 90);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(workload);

        service.process(request(ActionType.DELETE, LocalDate.of(2026, 1, 20), 30));

        assertEquals(60, workload.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
        verify(repository).save(workload);
    }

    @Test
    void processDeleteRemovesEmptyMonthAndYear() {
        TrainerWorkload workload = workloadWithSummary(2026, Month.JANUARY, 30);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(workload);

        service.process(request(ActionType.DELETE, LocalDate.of(2026, 1, 20), 30));

        assertTrue(workload.getYears().isEmpty());
        verify(repository).save(workload);
    }

    @Test
    void processDeleteDoesNotCreateOrSaveMissingTrainer() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(null);

        service.process(request(ActionType.DELETE, LocalDate.of(2026, 1, 20), 30));

        verify(repository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    void getMonthlyHoursSumsOnlyTheRequestedYearAndMonth() {
        TrainerWorkload workload = new TrainerWorkload("john.doe");
        workload.getYears().add(new YearSummary(2026));
        workload.getYears().get(0).getMonths().add(new MonthSummary(Month.JANUARY, 60));
        workload.getYears().get(0).getMonths().add(new MonthSummary(Month.FEBRUARY, 45));
        workload.getYears().add(new YearSummary(2025));
        workload.getYears().get(1).getMonths().add(new MonthSummary(Month.JANUARY, 120));
        when(repository.findByTrainerUsername("john.doe")).thenReturn(workload);

        MonthlyHoursResponse response = service.getMonthlyHours("john.doe", 2026, 1);

        assertEquals(new MonthlyHoursResponse("john.doe", 2026, 1, 60), response);
    }

    @Test
    void getWorkloadReturnsRepositoryResult() {
        TrainerWorkload workload = new TrainerWorkload("john.doe");
        when(repository.findByTrainerUsername("john.doe")).thenReturn(workload);

        assertSame(workload, service.getWorkload("john.doe"));
    }

    private static TrainerWorkload workloadWithSummary(int year, Month month, long duration) {
        TrainerWorkload workload = new TrainerWorkload("john.doe");
        YearSummary yearSummary = new YearSummary(year);
        yearSummary.getMonths().add(new MonthSummary(month, duration));
        workload.getYears().add(yearSummary);
        return workload;
    }

    private static WorkloadRequest request(ActionType action, LocalDate date, int duration) {
        return new WorkloadRequest("john.doe", "John", "Doe", true, date, duration, action);
    }
}

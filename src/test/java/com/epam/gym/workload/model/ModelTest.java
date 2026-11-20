package com.epam.gym.workload.model;

import org.junit.jupiter.api.Test;

import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelTest {

    @Test
    void monthSummaryStoresAndUpdatesDuration() {
        var summary = new MonthSummary(Month.JANUARY, 60);

        assertEquals(Month.JANUARY, summary.getMonth());
        assertEquals(60, summary.getTrainingSummaryDuration());

        summary.setTrainingSummaryDuration(90);

        assertEquals(90, summary.getTrainingSummaryDuration());
    }

    @Test
    void yearSummaryStoresYearAndAllowsMonths() {
        var summary = new YearSummary(2026);
        summary.getMonths().add(new MonthSummary(Month.JANUARY, 60));

        assertEquals(2026, summary.getYear());
        assertEquals(1, summary.getMonths().size());
    }

    @Test
    void trainerWorkloadStoresTrainerDetailsAndYears() {
        var workload = new TrainerWorkload("john.doe");
        workload.setTrainerFirstName("John");
        workload.setTrainerLastName("Doe");
        workload.setTrainerStatus(true);
        workload.getYears().add(new YearSummary(2026));

        assertEquals("john.doe", workload.getTrainerUsername());
        assertEquals("John", workload.getTrainerFirstName());
        assertEquals("Doe", workload.getTrainerLastName());
        assertEquals(true, workload.isTrainerStatus());
        assertEquals(1, workload.getYears().size());
    }
}

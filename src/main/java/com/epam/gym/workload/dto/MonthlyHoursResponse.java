package com.epam.gym.workload.dto;

public record MonthlyHoursResponse(String trainerUsername, int year, int month, long trainingSummaryDuration) {
}

package com.epam.gym.workload.model;

import java.util.ArrayList;
import java.util.List;

public class YearSummary {
    private int year;
    private List<MonthSummary> months = new ArrayList<>();

    public YearSummary(int year) { this.year = year; }
    public int getYear() { return year; }
    public List<MonthSummary> getMonths() { return months; }
}

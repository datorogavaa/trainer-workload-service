package com.epam.gym.workload.model;

import java.time.Month;

public class MonthSummary {
    private Month month;
    private long trainingSummaryDuration;

    public MonthSummary(Month month, long trainingSummaryDuration) {
        this.month = month;
        this.trainingSummaryDuration = trainingSummaryDuration;
    }
    public Month getMonth() { return month; }
    public long getTrainingSummaryDuration() { return trainingSummaryDuration; }
    public void setTrainingSummaryDuration(long d) { this.trainingSummaryDuration = d; }
}

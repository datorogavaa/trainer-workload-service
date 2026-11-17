package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.MonthlyHoursResponse;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.exception.NotFoundException;
import com.epam.gym.workload.model.MonthSummary;
import com.epam.gym.workload.model.TrainerWorkload;
import com.epam.gym.workload.model.YearSummary;
import com.epam.gym.workload.repository.WorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

@Service
public class WorkloadService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final WorkloadRepository repository;

    public WorkloadService(WorkloadRepository repository) {
        this.repository = repository;
    }

    public void process(WorkloadRequest request) {
        log.info("[operation] updateWorkload trainer={} action={} date={} duration={}",
                request.trainerUsername(), request.actionType(), request.trainingDate(), request.trainingDuration());

        repository.upsert(request.trainerUsername(), workload -> {
            workload.setTrainerFirstName(request.trainerFirstName());
            workload.setTrainerLastName(request.trainerLastName());
            workload.setTrainerStatus(request.isActive());
            apply(workload, request.trainingDate(), request.trainingDuration(), request.actionType());
            return workload;
        });

        log.info("[operation] updateWorkload completed for trainer={}", request.trainerUsername());
    }

    public MonthlyHoursResponse getMonthlyHours(String username, int year, int month) {
        log.info("[operation] getMonthlyHours trainer={} year={} month={}", username, year, month);
        TrainerWorkload workload = getWorkload(username);
        long total = workload.getYears().stream()
                .filter(y -> y.getYear() == year)
                .flatMap(y -> y.getMonths().stream())
                .filter(m -> m.getMonth().getValue() == month)
                .mapToLong(MonthSummary::getTrainingSummaryDuration)
                .sum();
        return new MonthlyHoursResponse(username, year, month, total);
    }

    public TrainerWorkload getWorkload(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("No workload found for trainer " + username));
    }

    private void apply(TrainerWorkload workload, LocalDate date, int duration, ActionType action) {
        YearSummary year = workload.getYears().stream()
                .filter(y -> y.getYear() == date.getYear()).findFirst()
                .orElseGet(() -> {
                    YearSummary y = new YearSummary(date.getYear());
                    workload.getYears().add(y);
                    return y;
                });
        Month monthKey = date.getMonth();
        MonthSummary month = year.getMonths().stream()
                .filter(m -> m.getMonth() == monthKey).findFirst()
                .orElseGet(() -> {
                    MonthSummary m = new MonthSummary(monthKey, 0);
                    year.getMonths().add(m);
                    return m;
                });

        long updated = action == ActionType.ADD
                ? month.getTrainingSummaryDuration() + duration
                : Math.max(0, month.getTrainingSummaryDuration() - duration);
        month.setTrainingSummaryDuration(updated);
    }
}

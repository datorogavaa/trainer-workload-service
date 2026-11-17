package com.epam.gym.workload.controller;

import com.epam.gym.workload.dto.MonthlyHoursResponse;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.model.TrainerWorkload;
import com.epam.gym.workload.service.WorkloadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Richardson level 2: resource-oriented URIs + proper HTTP verbs and status codes. */
@RestController
@RequestMapping("/api/trainers")
public class WorkloadController {

    private final WorkloadService service;

    public WorkloadController(WorkloadService service) {
        this.service = service;
    }

    /** Accepts a trainer workload change (ADD / DELETE). Returns 200 OK as per the contract. */
    @PostMapping("/workloads")
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody WorkloadRequest request) {
        service.process(request);
        return ResponseEntity.ok().build();
    }

    /** Hours for a trainer in a given month. */
    @GetMapping("/{username}/workloads/{year}/{month}")
    public MonthlyHoursResponse getMonthlyHours(@PathVariable String username,
                                                @PathVariable int year,
                                                @PathVariable int month) {
        return service.getMonthlyHours(username, year, month);
    }

    /** Full monthly summary of a trainer. */
    @GetMapping("/{username}/workloads")
    public TrainerWorkload getWorkload(@PathVariable String username) {
        return service.getWorkload(username);
    }
}

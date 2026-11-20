package com.epam.gym.workload.messaging;

import com.epam.gym.workload.dto.ActionType;
import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.service.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageListener {
    private static final Logger log = LoggerFactory.getLogger(WorkloadMessageListener.class);

    private final WorkloadService service;

    public WorkloadMessageListener(WorkloadService service) {
        this.service = service;
    }

    @JmsListener(destination = "gym.workload.updates")
    public void receive(WorkloadRequest request) {
        validate(request);
        log.info("[operation] received workload update trainer={} action={}",
                request.trainerUsername(), request.actionType());
        service.process(request);
    }

    private void validate(WorkloadRequest request) {
        if (request == null
                || request.trainerUsername() == null || request.trainerUsername().isBlank()
                || request.trainerFirstName() == null || request.trainerFirstName().isBlank()
                || request.trainerLastName() == null || request.trainerLastName().isBlank()
                || request.isActive() == null
                || request.trainingDate() == null
                || request.trainingDuration() == null
                || request.trainingDuration() <= 0
                || request.actionType() == null) {
            throw new IllegalArgumentException("Invalid workload message: required fields are missing");
        }
    }
}

package com.epam.gym.workload.messaging;

import com.epam.gym.workload.dto.WorkloadRequest;
import com.epam.gym.workload.service.WorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkloadMessageListener {
    private static final Logger log = LoggerFactory.getLogger(WorkloadMessageListener.class);
    private static final String TRANSACTION_ID_KEY = "transactionId";

    private final WorkloadService service;

    public WorkloadMessageListener(WorkloadService service) {
        this.service = service;
    }

    @JmsListener(destination = "gym.workload.updates")
    public void receive(WorkloadRequest request,
                        @Header(name = TRANSACTION_ID_KEY, required = false) String txId) {
        String resolvedTxId = (txId != null && !txId.isBlank())
                ? txId
                : UUID.randomUUID().toString();

        MDC.put(TRANSACTION_ID_KEY, resolvedTxId);
        try {
            validate(request);
            log.info("[txId={}] [operation] received workload update trainer={} action={}",
                    resolvedTxId, request.trainerUsername(), request.actionType());
            service.process(request);
        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
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
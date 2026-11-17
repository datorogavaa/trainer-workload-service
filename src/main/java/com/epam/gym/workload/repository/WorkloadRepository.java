package com.epam.gym.workload.repository;

import com.epam.gym.workload.model.TrainerWorkload;

import java.util.Optional;
import java.util.function.UnaryOperator;

public interface WorkloadRepository {
    Optional<TrainerWorkload> findByUsername(String username);

    /** Atomically creates or updates a trainer's workload. */
    TrainerWorkload upsert(String username, UnaryOperator<TrainerWorkload> updater);
}

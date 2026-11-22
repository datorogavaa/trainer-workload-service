package com.epam.gym.workload.repository;

import com.epam.gym.workload.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainerRepository extends MongoRepository<TrainerWorkload, String> {
    public TrainerWorkload findByTrainerUsername(String username);
}

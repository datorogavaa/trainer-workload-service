package com.epam.gym.workload.repository;

import com.epam.gym.workload.model.TrainerWorkload;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryWorkloadRepositoryTest {

    private final InMemoryWorkloadRepository repository = new InMemoryWorkloadRepository();

    @Test
    void findByUsernameReturnsEmptyForUnknownTrainer() {
        assertTrue(repository.findByUsername("nobody").isEmpty());
    }

    @Test
    void upsertCreatesAndThenUpdatesSameWorkload() {
        TrainerWorkload created = repository.upsert("john.doe", workload -> {
            workload.setTrainerFirstName("John");
            return workload;
        });
        TrainerWorkload updated = repository.upsert("john.doe", workload -> {
            workload.setTrainerLastName("Doe");
            return workload;
        });

        assertNotNull(created);
        assertEquals("John", updated.getTrainerFirstName());
        assertEquals("Doe", updated.getTrainerLastName());
        assertEquals(updated, repository.findByUsername("john.doe").orElseThrow());
    }
}

package com.epam.gym.workload.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryWorkloadRepositoryTest {

    private final InMemoryWorkloadRepository repository = new InMemoryWorkloadRepository();

    @Test
    void findByUsernameReturnsEmptyForUnknownTrainer() {
        assertTrue(repository.findByUsername("unknown").isEmpty());
    }

    @Test
    void upsertCreatesAndUpdatesTheSameWorkload() {
        var created = repository.upsert("john.doe", workload -> {
            workload.setTrainerFirstName("John");
            return workload;
        });
        var updated = repository.upsert("john.doe", workload -> {
            workload.setTrainerLastName("Doe");
            return workload;
        });

        assertSame(created, updated);
        assertEquals("John", updated.getTrainerFirstName());
        assertEquals("Doe", updated.getTrainerLastName());
    }
}

//package com.epam.gym.workload.repository;
//
//import com.epam.gym.workload.model.TrainerWorkload;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.UnaryOperator;
//
///** In-memory "database". ConcurrentHashMap.compute gives per-trainer atomic updates. */
//@Repository
//public class InMemoryWorkloadRepository implements WorkloadRepository {
//
//    private final ConcurrentHashMap<String, TrainerWorkload> store = new ConcurrentHashMap<>();
//
//    @Override
//    public Optional<TrainerWorkload> findByUsername(String username) {
//        return Optional.ofNullable(store.get(username));
//    }
//
//    @Override
//    public TrainerWorkload upsert(String username, UnaryOperator<TrainerWorkload> updater) {
//        return store.compute(username, (k, existing) ->
//                updater.apply(existing != null ? existing : new TrainerWorkload(k)));
//    }
//}

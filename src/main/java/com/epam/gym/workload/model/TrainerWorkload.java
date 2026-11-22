package com.epam.gym.workload.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "trainer_name_index", def = "{'trainerFirstName' : 1, 'trainerLastName' : 1}")
public class TrainerWorkload {

    @Id
    private String id;
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean trainerStatus;
    private List<YearSummary> years = new ArrayList<>();

    public TrainerWorkload(String username) { this.trainerUsername = username; }

}

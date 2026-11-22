package com.epam.gym.workload.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@CompoundIndex(name = "trainer_name_index", def = "{'trainerFirstName' : 1, 'trainerLastName' : 1}")
public class TrainerWorkload {

    @Id
    private Integer id;

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean trainerStatus;
    private List<YearSummary> years = new ArrayList<>();

    public TrainerWorkload(String username) { this.trainerUsername = username; }

    public String getTrainerUsername() { return trainerUsername; }
    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String v) { this.trainerFirstName = v; }
    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String v) { this.trainerLastName = v; }
    public boolean isTrainerStatus() { return trainerStatus; }
    public void setTrainerStatus(boolean v) { this.trainerStatus = v; }
    public List<YearSummary> getYears() { return years; }
}

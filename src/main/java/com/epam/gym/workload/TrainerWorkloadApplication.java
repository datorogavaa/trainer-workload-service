package com.epam.gym.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class TrainerWorkloadApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainerWorkloadApplication.class, args);
    }
}

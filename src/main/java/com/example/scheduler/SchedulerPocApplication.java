package com.example.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // <-- the "master switch". Without this, @Scheduled methods are ignored.
public class SchedulerPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerPocApplication.class, args);
    }
}

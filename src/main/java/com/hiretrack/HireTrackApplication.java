package com.hiretrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HireTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(HireTrackApplication.class, args);
    }
}
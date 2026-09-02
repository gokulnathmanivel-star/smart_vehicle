package com.svsbas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SvsbasApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvsbasApplication.class, args);
    }
}

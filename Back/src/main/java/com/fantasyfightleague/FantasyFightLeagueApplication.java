package com.fantasyfightleague;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FantasyFightLeagueApplication {

    public static void main(String[] args) {
        SpringApplication.run(FantasyFightLeagueApplication.class, args);
    }
}
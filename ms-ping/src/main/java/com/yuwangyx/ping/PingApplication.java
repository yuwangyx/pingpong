package com.yuwangyx.ping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingApplication.class, args);
    }
}
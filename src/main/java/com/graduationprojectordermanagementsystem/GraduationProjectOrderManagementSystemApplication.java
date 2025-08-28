package com.graduationprojectordermanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GraduationProjectOrderManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduationProjectOrderManagementSystemApplication.class, args);
    }

}

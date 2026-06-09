package org.example.foodypet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import jakarta.annotation.PostConstruct;

import java.util.TimeZone;
@EnableJpaAuditing
@SpringBootApplication
public class FoodypetApplication {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
    public static void main(String[] args) {
        SpringApplication.run(FoodypetApplication.class, args);
    }

}

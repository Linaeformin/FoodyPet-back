package org.example.foodypet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FoodypetApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodypetApplication.class, args);
    }

}

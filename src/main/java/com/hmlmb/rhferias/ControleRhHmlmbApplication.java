package com.hmlmb.rhferias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hmlmb"})
@EntityScan("com.hmlmb")
@EnableJpaRepositories("com.hmlmb")
public class ControleRhHmlmbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControleRhHmlmbApplication.class, args);
    }

}

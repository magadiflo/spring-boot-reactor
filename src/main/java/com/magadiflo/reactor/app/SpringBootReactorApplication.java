package com.magadiflo.reactor.app;

import com.magadiflo.reactor.app.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootReactorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Flux<String> names = Flux.just("Martín Flores", "Liz Gonzales", "Candi Abanto", "Isela Pimentel", "Bruce Lee", "Bruce Willis");

            Flux<User> users = names.map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                    .filter(user -> user.getName().equalsIgnoreCase("Bruce"));

            names.subscribe(LOG::info);
        };
    }

}

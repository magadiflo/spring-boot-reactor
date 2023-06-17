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
            Flux<User> users = Flux.just("Martín Flores", "Liz Gonzales", "Candi Abanto", "Isela Pimentel", "Bruce Lee", "Bruce Willis")
                    .map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                    .filter(user -> user.getName().equalsIgnoreCase("Bruce"))
                    .doOnNext(user -> {
                        if (user == null) {
                            throw new RuntimeException("Usuario no pueden ser null");
                        }
                        System.out.println(user);
                    });

            users.subscribe(
                    user -> LOG.info(user.toString()),
                    error -> LOG.error(error.getMessage()),
                    new Runnable() {
                        @Override
                        public void run() {
                            LOG.info("Ha finalizado la ejecución del observable con éxito!");
                        }
                    });
        };
    }

}

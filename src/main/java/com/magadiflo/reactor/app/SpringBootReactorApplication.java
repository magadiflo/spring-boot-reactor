package com.magadiflo.reactor.app;

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
            Flux<String> nombres = Flux.just("Martín", "", "Candi", "Iselita")
                    .doOnNext(name -> {
                        if (name.isEmpty()) {
                            throw new RuntimeException("Nombre no pueden ser vacíos");
                        }
                        System.out.println(name);
                    });

            nombres.subscribe(LOG::info, error -> LOG.error(error.getMessage()));
        };
    }

}

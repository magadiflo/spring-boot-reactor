package com.magadiflo.reactor.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Flux<String> nombres = Flux.just("Martín", "Livved", "Candi", "Iselita")
                    .doOnNext(System.out::println);

            nombres.subscribe();
        };
    }

}

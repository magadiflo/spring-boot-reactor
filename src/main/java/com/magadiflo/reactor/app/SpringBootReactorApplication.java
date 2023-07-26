package com.magadiflo.reactor.app;

import com.magadiflo.reactor.app.models.Comment;
import com.magadiflo.reactor.app.models.User;
import com.magadiflo.reactor.app.models.UserComment;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class SpringBootReactorApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootReactorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            this.backpressureExampleUsingOperator();
        };
    }

    private void backpressureExampleUsingOperator() {
        Flux.range(1, 10)
                .log()
                .limitRate(2)
                .subscribe();
    }

    private void backpressureExampleUsingSubscriber() {
        Flux.range(1, 10)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    private Integer limit = 2;
                    private Integer consumed = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        this.subscription.request(this.limit);
                    }

                    @Override
                    public void onNext(Integer rangeNumber) {
                        LOG.info(rangeNumber.toString());
                        this.consumed++;
                        if (this.consumed.equals(this.limit)) {
                            this.consumed = 0;
                            this.subscription.request(this.limit);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void infiniteIntervalExampleFromCreate() {
        Flux.create(emitter -> { //emitter, objeto que nos permite crear nuestro observable
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Integer randomNumber = (int) (Math.random() * 10 + 1);
                            emitter.next(randomNumber);     // <-- (1) Emite los valores
                            if (randomNumber.equals(5)) {
                                timer.cancel();
                                emitter.error(new InterruptedException("¡Error, se detuvo el flux por el 5!")); //<-- (2) Emite un error cancelando el Flux
                            }
                            if (randomNumber.equals(10)) {
                                timer.cancel();
                                emitter.complete();            // <-- (3) Finaliza la ejecución del Flux
                            }
                        }
                    }, 1000, 1000);
                })
                .subscribe(
                        next -> LOG.info(next.toString()),  // (1) Recibe los valores emitidos
                        error -> LOG.error(error.getMessage()),      // (2) Recibe el error generado
                        () -> LOG.info("¡Fín, se obtuvo 10!"));      // (3) Se ejecuta luego de que ha finalizado la ejecución del Flux

    }

    private void infiniteIntervalExample() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                .flatMap(i -> {
                    if (i == 5) {
                        return Flux.error(new InterruptedException("Solo hasta 5"));
                    }
                    return Flux.just(i);
                })
                .map(i -> "Hola " + i)
                .retry(2)
                .doOnTerminate(latch::countDown)
                .subscribe(
                        LOG::info,
                        e -> LOG.error(e.getMessage())
                );

        latch.await();
    }

    private void delayElementsExample() {
        Flux<Integer> range = Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(integer -> LOG.info(integer.toString()));

        range.blockLast();
    }

    private void intervalExample() {
        Flux<Integer> range = Flux.range(1, 12);
        Flux<Long> delay = Flux.interval(Duration.ofSeconds(1));

        range.zipWith(delay, (numRange, valDelay) -> numRange)
                .doOnNext(integer -> LOG.info(integer.toString()))
                .blockLast(); // se subscribe al flujo, pero bloquea hasta el último elemento que se emita
    }

    private void zipWithAndRanges() {
        Flux.just(1, 2, 3, 4) // numSequence
                .map(number -> number * 2)
                .zipWith(Flux.range(0, 4)/*numRange*/, (numSequence, numRange) -> String.format("[1] flux: %d, [2] flux: %d", numSequence, numRange))
                .subscribe(LOG::info);
    }

    private void combinedZipWithForm2() {
        Mono<User> userMono = Mono.fromCallable(() -> new User("Rocky", "Balboa")); // Otra forma de crear un mono
        Mono<Comment> commentMono = Mono.fromCallable(() -> {
            Comment comment = new Comment();
            comment.addComment("Hola Ivan, qué tal!");
            comment.addComment("Cuando pactamos otra pelea?");
            comment.addComment("me avisas, estaré pendiente, saludos.");
            return comment;
        });

        Mono<UserComment> userCommentMono = userMono.zipWith(commentMono)
                .map(tuple -> {
                    User u = tuple.getT1();
                    Comment c = tuple.getT2();
                    return new UserComment(u, c);
                });
        userCommentMono.subscribe(userComment -> LOG.info(userComment.toString()));
    }

    private void combinedZipWithForm1() {
        Mono<User> userMono = Mono.fromCallable(() -> new User("Rocky", "Balboa")); // Otra forma de crear un mono
        Mono<Comment> commentMono = Mono.fromCallable(() -> {
            Comment comment = new Comment();
            comment.addComment("Hola Ivan, qué tal!");
            comment.addComment("Cuando pactamos otra pelea?");
            comment.addComment("me avisas, estaré pendiente, saludos.");
            return comment;
        });

        Mono<UserComment> userCommentMono = userMono.zipWith(commentMono, UserComment::new);
        userCommentMono.subscribe(userComment -> LOG.info(userComment.toString()));
    }

    private void combinedUserAndCommentWithFlatMap() {
        Mono<User> userMono = Mono.fromCallable(() -> new User("Rocky", "Balboa"));
        Mono<Comment> commentMono = Mono.fromCallable(() -> {
            Comment comment = new Comment();
            comment.addComment("Hola Ivan, qué tal!");
            comment.addComment("Cuando pactamos otra pelea?");
            comment.addComment("me avisas, estaré pendiente, saludos.");
            return comment;
        });

        // Usando flatMap - flatMap
        Mono<UserComment> userCommentMonoFlatMapFlatMap = userMono.flatMap(user -> commentMono.flatMap(comment -> Mono.just(new UserComment(user, comment))));
        userCommentMonoFlatMapFlatMap.subscribe(userComment -> LOG.info(userComment.toString()));

        // Usando flatMap - Map
        Mono<UserComment> userCommentMonoFlatMapMap = userMono.flatMap(user -> commentMono.map(comment -> new UserComment(user, comment)));
        userCommentMonoFlatMapMap.subscribe(userComment -> LOG.info(userComment.toString()));
    }

    private void convertFluxToMonoList() {
        List<User> userList = List.of(
                new User("Martín", "Flores"),
                new User("Liz", "Gonzales"),
                new User("Candi", "Abanto"),
                new User("Isela", "Pimentel"),
                new User("Bruce", "Lee"),
                new User("Bruce", "Willis"));
        Flux.fromIterable(userList)
                .collectList()
                .subscribe(users -> LOG.info(users.toString()));
    }

    private void fromUserToString() {
        List<User> userList = List.of(
                new User("Martín", "Flores"),
                new User("Liz", "Gonzales"),
                new User("Candi", "Abanto"),
                new User("Isela", "Pimentel"),
                new User("Bruce", "Lee"),
                new User("Bruce", "Willis"));
        Flux<User> users = Flux.fromIterable(userList);

        Flux<String> names = users.map(user -> String.format("%s %s", user.getName(), user.getLastName()));

        names.subscribe(LOG::info);
    }

    private void flatMapExample() {
        List<String> namesList = List.of("Martín Flores", "Liz Gonzales", "Candi Abanto", "Isela Pimentel", "Bruce Lee", "Bruce Willis");
        Flux<String> names = Flux.fromIterable(namesList);

        Flux<User> users = names.map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                .flatMap(user -> {
                    if (user.getName().equalsIgnoreCase("bruce")) {
                        return Mono.just(user);
                    }
                    return Mono.empty();
                });

        users.subscribe(user -> LOG.info(user.toString()));
    }

    private void iterableExample() {
        List<String> namesList = List.of("Martín Flores", "Liz Gonzales", "Candi Abanto", "Isela Pimentel", "Bruce Lee", "Bruce Willis");
        Flux<String> names = Flux.fromIterable(namesList);

        Flux<User> users = names.map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                .filter(user -> user.getName().equalsIgnoreCase("Bruce"));

        users.subscribe(user -> LOG.info(user.toString()));
    }

}

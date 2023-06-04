# Sección 02: Reactor API: Programación reactiva desde la base

Proyecto realizado en el curso de **Programación Reactiva con Spring Boot y Spring WebFlux** de Andrés Guzmán (Udemy)

### Reactive Streams

Reactive Streams es una iniciativa para proporcionar un estándar para el procesamiento asíncrono de flujos con
contrapresión sin bloqueo. ``Es la especificación para la programación reactiva``.

### Project Reactor

Reactor es una biblioteca reactiva de cuarta generación, **basada en la especificación Reactive Streams**, para crear
aplicaciones sin bloqueo en la JVM.``Es la implementación.``

Si hacemos una analogía, Reactive Streams es a JPA como Project Reactor es a Hibernate, en pocas palabras, son la
**especificación** e **implementación** respectivamente.

## Creando Proyecto con Reactor API

Para usar el Proyecto Reactor en Spring Boot solo necesitamos agregar la siguiente dependencia que fue obtenida de la
página de [Projectreactor.io](https://projectreactor.io/docs/core/release/reference/#getting):

````xml

<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
````

## Creando un Stream Flux Observable

En este proyecto de Spring Boot trabajaremos netamente en consola, para eso podemos implementar la interfaz funcional
**CommandLineRunner** o en su defecto podemos crear un @Bean donde implementemos dicha interfaz:

````java

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
````

**NOTA**

- **Flux**, es un publisher, por lo tanto un observable. Emite una secuencia asíncrona de 0 a N elementos **(onNext)** y
  termina con una señal **(onComplete)**. También puede terminar con una seña **(onError)**.
- Creamos un Flux de Strings.
- No sucederá nada hasta que nos subscribamos (.subscribe()).
- **.doOnNext()**, es una especie de callback que se ejecuta cuando un Publisher emite un elemento, pero no afecta el
  flujo, es decir, devuelve el publicador original inmediatamente.

**IMPORTANTE**
> Como estamos iniciando en el aprendizaje de programación reactiva, estamos usando el método ``.subscribe()``, pero
> según tengo entendido, en un proyecto ya real, nosotros nunca haremos eso, ya que quien se subscribirá serán quienes
> llamen a las API Rest por ejemplo, o como más adelante usaremos el mismo Thymeleaf, será quien haga el
> subscribe() para desencadenar la ejecución.
>
> Métodos que devuelven un tipo Publisher (Mono, Flux) no deberían subscribirse(subscribe o block) porque ello
> podría romper la cadena del publicador.

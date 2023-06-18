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

## El método subscribe()

Cuando hacemos el **subscribe()** empezamos a observar, por lo tanto, se trata de un consumidor, un **Observer** que
consume cada elemento que emite el **Observable**. No solo puede consumir, sino también puede manejar cualquier tipo
de error que pueda ocurrir.

````java

@SpringBootApplication
public class SpringBootReactorApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootReactorApplication.class);

    /* other code */

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
````

## El evento onComplete

El evento onComplete se ejecuta solo si finaliza la emisión de todos los elementos del flujo. Si ocurre un error en el
proceso, el evento onComplete no se ejecuta, sino el evento onError.

````java

@SpringBootApplication
public class SpringBootReactorApplication {

    /* other code */

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Flux<String> nombres = Flux.just("Martín", "Lidia", "Candi", "Iselita")
                    .doOnNext(name -> {
                        if (name.isEmpty()) {
                            throw new RuntimeException("Nombre no pueden ser vacíos");
                        }
                        System.out.println(name);
                    });

            nombres.subscribe(
                    LOG::info,
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
````

## El operador map

Recibe cada elemento y lo transforma a otro tipo de elemento. **Las modificaciones que hacemos con los operadores
no modifica el flujo original,** más bien lo que hace es **retornar una nueva instancia** de otro flux a partir
del original, pero con los datos modificados.

Para este ejemplo, transformaremos un flujo de Strings a un objeto del tipo User

````java
public class User {
    private String name;
    private String lastName;

    public User(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    /* setters and getters methods */
    /* toString() method */
}
````

````java

@SpringBootApplication
public class SpringBootReactorApplication {

    /* other code */

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Flux<User> users = Flux.just("Martín", "Lidia", "Candi", "Iselita")
                    .map(name -> new User(name.toUpperCase(), null))
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
````

## El operador filter

No permite filtrar valores, como parámetro recibe un predicate (función de valor booleano) de un argumento.

````java

@SpringBootApplication
public class SpringBootReactorApplication {
    /* omitted code */

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Flux<User> users = Flux.just("Martín Flores", "Liz Gonzales", "Candi Abanto", "Isela Pimentel", "Bruce Lee", "Bruce Willis")
                    .map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                    .filter(user -> user.getName().equalsIgnoreCase("Bruce")) //<-- filtramos por el nombre "Bruce"
                    .doOnNext(user -> {
                        if (user == null) {
                            throw new RuntimeException("Usuario no pueden ser null");
                        }
                        System.out.println(user);
                    });

            /* omitted code */
        };
    }
}
````

## Los observables son inmutables

Una de las características que tienen los streams reactive es la inmutabilidad. Cuando tenemos un flujo de datos, y
empezamos a usar operadores para transformar el flujo, no es que se vaya a cambiar el flujo original, sino que,
se van creando nuevas instancias de flujos por cada nuevo operador que usemos.

En el siguiente ejemplo obervamos un flujo original de Strings, al que le aplicamos posteriormente un map y un filter,
ahora, nos subscribimos al flujo de Strings y vemos que los valores se mantienen tal cual.

````java

@SpringBootApplication
public class SpringBootReactorApplication {

    /* omitted code */

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
````

## Creando un Flux (Observable) a partir de un List o Iterable

Podemos usar la función **.fromIterable(...)** para convertir una lista en un **Flux**.

````java

@SpringBootApplication
public class SpringBootReactorApplication {

    /* omitted code */

    @Bean
    public CommandLineRunner run() {
        return args -> {
            List<String> namesList = List.of("Martín Flores", "Liz Gonzales", "Candi Abanto", "Isela Pimentel", "Bruce Lee", "Bruce Willis");
            Flux<String> names = Flux.fromIterable(namesList);

            Flux<User> users = names.map(name -> new User(name.split(" ")[0], name.split(" ")[1]))
                    .filter(user -> user.getName().equalsIgnoreCase("Bruce"));

            users.subscribe(user -> LOG.info(user.toString()));
        };
    }
}
````

## El operador flatMap

Con el flatMap, convertimos un dato a otro flujo (Mono o Flux) y por debajo, el **flatMap aplanará el flujo para
convertir todos los flujos en uno solo.** La diferencia con el map es que con map trabajamos con datos como
normalmente hemos venido trabajando, mientras que con flatMap, trabajamos con flujos.

````java

@SpringBootApplication
public class SpringBootReactorApplication {

    /* omitted code */

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

    /* omitted code */

}
````

## Convirtiendo un Flux List a un Flux String con operador map

````java

/* omitted code */

@SpringBootApplication
public class SpringBootReactorApplication {

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
}
````

## Convirtiendo un Observable Flux a Mono

En el ejemplo convertiremos un Flux de usuarios, a un mono de tipo List de usuario, es decir, en vez de que se emita
cada elemento de la lista, vamos a emitir de una vez la lista completa.

````java

@SpringBootApplication
public class SpringBootReactorApplication {
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
}
````
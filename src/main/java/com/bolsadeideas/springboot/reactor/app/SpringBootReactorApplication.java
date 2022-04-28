package com.bolsadeideas.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Martín Flores");
		usuariosList.add("Gaspar Tello");
		usuariosList.add("Rachul Zúñiga");
		usuariosList.add("Tinkler Caldas");
		usuariosList.add("Alicia Flores");
		usuariosList.add("Gaspar Díaz");
		usuariosList.add("Gaspar Flores");
		
		Flux<String> nombres = Flux.fromIterable(usuariosList); //Flux.just("Martín Flores", "Gaspar Tello", "Rachul Zúñiga", "Tinkler Caldas", "Alicia Flores", "Gaspar Díaz", "Gaspar Flores");
		
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
			.filter(usuario -> usuario.getNombre().toLowerCase().equals("gaspar"))
			.doOnNext(usuario -> {
				if(usuario == null) {
					throw new RuntimeException("Nombres no pueden ser vacíos.");
				}					
				System.out.println(usuario);						
			})
			.map(usuario -> {
				String nombre = usuario.getNombre().toLowerCase();
				usuario.setNombre(nombre);
				return usuario;					
			});
				

		usuarios.subscribe(
				e -> log.info(e.toString()), 
				error -> log.error(error.getMessage()), 
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecución del observable con éxito!");
					}
				});
	}

}

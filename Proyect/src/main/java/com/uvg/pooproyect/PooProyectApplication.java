package com.uvg.pooproyect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Esta clase reemplaza al antiguo Main.java de consola.
// En lugar de ejecutar pruebas por System.out.println, ahora
// levanta un servidor web que expone el portal (carpeta static)
// y los endpoints REST (paquete controller).
@SpringBootApplication
public class PooProyectApplication {
    public static void main(String[] args) {
        SpringApplication.run(PooProyectApplication.class, args);
        System.out.println("---Sistema Gestiones Universitario---");
        System.out.println("Portal disponible en: http://localhost:8080");
    }
}

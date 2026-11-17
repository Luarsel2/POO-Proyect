package com.uvg.pooproyect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// usuario de estudiante es: 26547 y contraseña es prueba26547@uvg.edu.gt
// usuario de admin es: admin y contraseña admin123

@SpringBootApplication
public class PooProyectApplication {
    public static void main(String[] args) {
        SpringApplication.run(PooProyectApplication.class, args);
        System.out.println("---Sistema Gestiones Universitario---");
        System.out.println("Portal disponible en: http://localhost:8080");
    }
}

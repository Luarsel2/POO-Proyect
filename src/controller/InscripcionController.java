package controller;

import model.Estudiante;
import model.Curso;

public class InscripcionController {

    public String procesarInscripcion(Estudiante estudiante, Curso curso) {
        if (estudiante == null || curso == null) {
            return "Error: Datos de estudiante o curso inválidos.";
        }

        // Simulación de validación
        return "Inscripción del curso '" + curso.getNombre() + "' completada para " + estudiante.getNombre();
    }
}
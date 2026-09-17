package controller;

import model.Estudiante;
import model.Curso;
import model.Inscripcion;
import java.util.ArrayList;
import java.util.List;

public class EstudianteController {

    // Simulación de base de datos en memoria
    private List<Estudiante> listaEstudiantes;

    public EstudianteController() {
        this.listaEstudiantes = new ArrayList<>();
    }

    // Registrar estudiante interactuando directamente con el modelo
    public String registrarEstudiante(int id, String nombre, String apellido, String fechaNacimiento, 
                                      String correo, int telefono, String direccion, int carnet, 
                                      int fechaIngreso, String estado, double promedioGeneral, 
                                      Inscripcion inscripcion, Curso curso, int idHistorial) {

        Estudiante nuevoEstudiante = new Estudiante(
            id, nombre, apellido, fechaNacimiento, correo, telefono, 
            direccion, carnet, fechaIngreso, estado, promedioGeneral, 
            inscripcion, curso, idHistorial
        );

        listaEstudiantes.add(nuevoEstudiante);
        return "Estudiante " + nombre + " " + apellido + " registrado exitosamente. Carnet: " + carnet;
    }

    // Ejecutar lógica de negocio del modelo
    public String inscribirCursoAEstudiante(int carnet) {
        Estudiante estudiante = buscarPorCarnet(carnet);

        if (estudiante != null) {
            estudiante.inscribirCurso();
            return "Curso inscrito exitosamente para el carnet: " + carnet;
        }

        return "Error: Estudiante con carnet " + carnet + " no encontrado.";
    }

    // Buscar estudiante por carnet
    public Estudiante buscarPorCarnet(int carnet) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getCarnet() == carnet) {
                return e;
            }
        }
        return null;
    }

    public List<Estudiante> obtenerTodos() {
        return listaEstudiantes;
    }
}
package com.uvg.pooproyect.dto;

// Todas las peticiones que llegan desde el portal (formularios del frontend)
// agrupadas en un solo archivo como "records" de Java: son clases simples
// de solo lectura, pensadas unicamente para recibir los datos del JSON
// que manda el navegador y pasarlos a los controladores/modelos.
public class Requests {

    public record EstudianteRequest(
            int id, String nombre, String apellido, String fechaNacimiento, String correo,
            int telefono, String direccion, int carnet, int fechaIngreso, String estado,
            double promedioGeneral, int idHistorial) {}

    public record ProfesorRequest(
            int id, String nombre, String apellido, String fechaNacimiento, String correo,
            int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion,
            String especialidad, String materias) {}

    public record AdministracionRequest(
            int id, String nombre, String apellido, String fechaNacimiento, String correo,
            int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion,
            String puesto, String departamento) {}

    public record CursoRequest(String nombre, int creditos) {}

    public record FacultadRequest(String nombre) {}

    public record DepartamentoRequest(String nombreDepartamento) {}

    public record CarreraRequest(String nombre, String totalCreditos, int duracionPromedio) {}

    public record PlanDeEstudioRequest(int year, String estado) {}

    public record SeccionRequest(
            int idSeccion, int codigo, int cupoMaximo, int codigoProfesor) {}

    public record HorarioRequest(
            int idHorario, String diaSemana, String horaInicio, String horaFin) {}

    public record PeriodoRequest(
            int idPeriodo, String nombre, String fechaInicio, String fechaFin,
            String fechaInicioInscripcion, String fechaFinInscripcion, String estado) {}

    public record InscripcionRequest(
            int id, String fechaDeInscripcion, String estado, int totalCreditos,
            int carnetEstudiante, int idPeriodo) {}

    public record DetalleInscripcionRequest(
            int idDetalle, String fechaInscripcion, String estado, int idSeccion) {}
}

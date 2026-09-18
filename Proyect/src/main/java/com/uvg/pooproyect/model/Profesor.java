package com.uvg.pooproyect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Profesor extends Empleado {
    private String especialidad;
    private String materias;
    private Seccion seccion;

    public Profesor(int id, String nombre, String apellido, String fechaNacimiento, String correo,int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion, String especialidad, String materias, Seccion seccion){
        super(id, nombre, apellido, fechaNacimiento, correo, telefono, direccion, codigoDeEmpleado, fechaDeContratacion);

        this.especialidad = especialidad;
        this.materias = materias;
        this.seccion = seccion;
    }

    public void asignarNota(){
        System.out.println("Asignando nota...");
    }
    public void consultarCursos(){
        System.out.println("Consultando cursos...");
    }

    public String getEspecialidad(){
        return especialidad;
    }
    public void setEspecialidad(String especialidad){
        this.especialidad = especialidad;
    }

    public String getMaterias(){
        return materias;
    }
    public void setMaterias(String materias){
        this.materias = materias;
    }

    // @JsonIgnore: Seccion tambien apunta de vuelta a este Profesor
    // (profesor.getSeccion().getProfesor()...) lo que provoca un bucle
    // infinito al convertir a JSON. Se ignora este lado; la relacion sigue
    // visible consultando /api/secciones.
    @JsonIgnore
    public Seccion getSeccion(){
        return seccion;
    }
    public void setSeccion(Seccion seccion){
        this.seccion = seccion;
    }

}

package com.uvg.pooproyect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Curso {
    private String nombre;
    private int creditos;
    private Estudiante estudiante;

    public Curso(String nombre, int creditos, Estudiante estudiante){
        this.nombre = nombre;
        this.creditos = creditos;
        this.estudiante = estudiante;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public int getCreditos(){
        return creditos;
    }
    public void setCreditos(int creditos){
        this.creditos = creditos;
    }

    // @JsonIgnore: Estudiante tambien apunta de vuelta a este Curso
    // (curso.getEstudiante().getCurso()...) lo que provoca un bucle
    // infinito al convertir a JSON. Se ignora este lado; la relacion sigue
    // visible consultando /api/estudiantes.
    @JsonIgnore
    public Estudiante getEstudiante(){
        return estudiante;
    }
    public void setEstudiante(Estudiante estudiante){
        this.estudiante = estudiante;
    }
}

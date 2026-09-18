package com.uvg.pooproyect.model;

import java.util.ArrayList;
import java.util.List;


public class Inscripcion {
    private int id;
    private String fechaDeInscripcion;
    private String estado;
    private int totalCreditos;
    private Estudiante estudiante; // Relacion con la clase estudiante
    private PeriodoAcademico periodoAcademico;
    // Composicion - Inscripcion posee lista con detalles
    private List<DetalleInscripcion> detalles;

    public Inscripcion(int id, String fechadeInscripcion, String estado, int totalCreditos, Estudiante estudiante, PeriodoAcademico periodoAcademico){
        this.id = id;
        this.fechaDeInscripcion = fechadeInscripcion;
        this.estado = estado;
        this.totalCreditos = totalCreditos;
        this.estudiante = estudiante;
        this.periodoAcademico = periodoAcademico;
        this.detalles = new ArrayList<>(); // empieza vacia la lista
    }

    // Metodo para composicion - Agrega el detalle directo a la inscripcion
    public void agregarDetalle(int id, String fechaInscripcion, String estado, Seccion seccion){
        DetalleInscripcion detalle = new DetalleInscripcion(id, fechaInscripcion, estado, seccion);
        this.detalles.add(detalle);
    }

    public void asignarSeccion(){
        System.out.println("Asignando seccion...");
    }

    public void eliminarSeccion(){
        System.out.println("Eliminando seccion...");
    }

    public int calcularTotalCreditos(){
        return this.totalCreditos;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getFechaDeInscripcion(){
        return fechaDeInscripcion;
    }
    public void setFechaDeInscripcion(String fechaDeInscripcion){
        this.fechaDeInscripcion = fechaDeInscripcion;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    public int getTotalCreditos(){
        return totalCreditos;
    }
    public void setTotalCreditos(int totalCreditos){
        this.totalCreditos = totalCreditos;
    }

    public Estudiante getEstudiante(){
        return estudiante;
    }
    public void setEstudiante(Estudiante estudiante){
        this.estudiante = estudiante;
    }

    public PeriodoAcademico getPeriodoAcademico(){
        return periodoAcademico;
    }
    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico){
        this.periodoAcademico = periodoAcademico;
    }

    public List<DetalleInscripcion> getDetalles(){
        return detalles;
    }

}

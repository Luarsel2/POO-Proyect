package com.uvg.pooproyect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PeriodoAcademico {
    private int idPeriodo;
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
    private String fechaInicioInscripcion;
    private String fechaFinInscripcion;
    private String estado;
    private Inscripcion inscripcion;

    public PeriodoAcademico(int idPeriodo, String nombre, String fechaInicio, String fechaFin, String fechaInicioInscripcion, String fechaFinInscripcion, String estado, Inscripcion inscripcion){
        this.idPeriodo = idPeriodo;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.estado = estado;
        this.inscripcion = inscripcion;
    }
    // verificador si el periodo general esta activo
    public boolean estaActivo(){
        return this.estado != null && (this.estado.equalsIgnoreCase("ACTIVO") || this.estado.equalsIgnoreCase("INSCRIPCION"));
    }
    // para verificar si se pueden realizar inscripciones en el momento
    public boolean inscripcionDisponible(){
        return this.estado != null && this.estado.equalsIgnoreCase("INSCRIPCION");
    }
    // cambia el estado del periodo para permitir inscripciones
    public void abrirInscripciones(){
        this.estado = "ACTIVO";
        System.out.println("Inscripciones disponibles para el periodo: "+this.nombre);
    }

    public void cerrarInscripciones(){
        this.estado = "ACTIVO";
        System.out.println("Inscripciones cerradas para el periodo: "+this.nombre);
    }

    public int getIdPeriodo(){
        return idPeriodo;
    }
    public void setIdPeriodo(int idPeriodo){
        this.idPeriodo = idPeriodo;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getFechaInicio(){
        return fechaInicio;
    }
    public void setFechaInicio(String fechaInicio){
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin(){
        return fechaFin;
    }
    public void setFechaFin(String fechaFin){
        this.fechaFin = fechaFin;
    }

    public String getFechaInicioInscripcion(){
        return fechaInicioInscripcion;
    }
    public void setFechaInicioInscripcion(String fechaInicioInscripcion){
        this.fechaInicioInscripcion = fechaInicioInscripcion;
    }

    public String getFechaFinInscripcion(){
        return fechaFinInscripcion;
    }
    public void setFechaFinInscripcion(String fechaFinInscripcion){
        this.fechaFinInscripcion = fechaFinInscripcion;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    // @JsonIgnore: Inscripcion tambien apunta de vuelta a este PeriodoAcademico
    // (periodo.getInscripcion().getPeriodoAcademico()...) lo que provoca un
    // bucle infinito al convertir a JSON. Se ignora este lado; la relacion
    // sigue visible consultando /api/inscripciones.
    @JsonIgnore
    public Inscripcion getInscripcion(){
        return inscripcion;
    }
    public void setInscripcion(Inscripcion inscripcion){
        this.inscripcion = inscripcion;
    }

}

package com.uvg.pooproyect.model;

import java.util.ArrayList;
import java.util.List;

public class Carrera {
    private String nombre;
    private String totalCreditos;
    private int duracionPromedio;
    private List<PlanDeEstudio> planesDeEstudios;

    public Carrera(String nombre, String totalCreditos, int duracionPromedio){
        this.nombre = nombre;
        this.totalCreditos = totalCreditos;
        this.duracionPromedio = duracionPromedio;
        this.planesDeEstudios = new ArrayList<>();
    }

    public void agregarPlanDeEstudio(int year, String estado){
        PlanDeEstudio plan = new PlanDeEstudio(year, estado);
        this.planesDeEstudios.add(plan);
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getTotalCreditos(){
        return totalCreditos;
    }
    public void setTotalCreditos(String totalCreditos){
        this.totalCreditos = totalCreditos;
    }

    public int getDuracionPromedio(){
        return duracionPromedio;
    }
    public void setDuracionPromedio(int duracionPromedio){
        this.duracionPromedio = duracionPromedio;
    }

    public List<PlanDeEstudio> getPlanesDeEstudios(){
        return planesDeEstudios;
    }
}

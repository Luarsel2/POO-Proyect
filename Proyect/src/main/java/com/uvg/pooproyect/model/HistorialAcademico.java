package com.uvg.pooproyect.model;

import java.util.ArrayList;
import java.util.List;

public class HistorialAcademico {
    private int idHistorial;
    private List<Curso> cursosAprobados;

    public HistorialAcademico(int idHistorial){
        this.idHistorial = idHistorial;
        this.cursosAprobados = new ArrayList<>();
    }
    // dependencia metodo 1
    // recibe un curso como parametro temporal para verificar si fue aprobado
    public boolean verificarCursoAprobado(Curso curso){
        if (curso == null){
            return false;
        }
        return this.cursosAprobados.contains(curso);
    }
    // dependencia metodo 2
    // recibe un curso como parametro para evaluar sus prerrequisitos
    public boolean verificarPrerrequisitos(Curso curso){
        if (curso == null){
            return false;
        } // logica de validacion si el curso requiere el prerrequisito
          // se evaluara si existe dentro de la lista de cursos aprobados
        return true;
    }
    // metodo que nos servira de apoyo para agregar cursos aprobados al mero historial
    public void agregarCursoAprobado(Curso curso){
        if (curso != null && !this.cursosAprobados.contains(curso)){
            this.cursosAprobados.add(curso);
        }
    }

    public int getIdHistorial(){
        return idHistorial;
    }
    public void setIdHistorial(int idHistorial){
        this.idHistorial = idHistorial;
    }

    public List<Curso> getCursosArobados(){
        return cursosAprobados;
    }
}

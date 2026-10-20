package com.uvg.pooproyect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Profesor extends Empleado {
    private String especialidad;
    private String materias;
    
    private static final String ARCHIVO = "profesores.csv";
    private List<Profesor> listaProfesores = new ArrayList<>();

    public Profesor(int id, String nombre, String apellido, String fechaNacimiento, String correo, int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion, String especialidad, String materias) {
        super(id, nombre, apellido, fechaNacimiento, correo, telefono, direccion, codigoDeEmpleado, fechaDeContratacion);
        this.especialidad = especialidad;
        this.materias = materias;
    }

    public void asignarNota() {
        System.out.println("Asignando nota...");
    }

    public void consultarCursos() {
        System.out.println("Consultando cursos...");
    }

    public Profesor buscarPorCodigo(int codigo) {
        for (Profesor p : listaProfesores) {
            if (p.getCodigoDeEmpleado() == codigo) {
                return p;
            }
        }
        return null;
    }

    private void guardar() {
        //  persistencia en archivo profesores.csv
    }

    public void cargar() {
        // lectura desde archivo profesores.csv
    }

    public Map<String, String> registrarProfesor() {
        // registro del profesor y retornar mensaje/estado
        return Map.of("mensaje", "Profesor registrado exitosamente");
    }

    public List<Profesor> obtenerTodos() {
        return listaProfesores;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getMaterias() {
        return materias;
    }

    public void setMaterias(String materias) {
        this.materias = materias;
    }

    public List<Profesor> getListaProfesores() {
        return listaProfesores;
    }

    public void setListaProfesores(List<Profesor> listaProfesores) {
        this.listaProfesores = listaProfesores;
    }
}
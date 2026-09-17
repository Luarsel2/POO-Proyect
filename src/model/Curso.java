package model;

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

    public Estudiante getEstudiante(){
        return estudiante;
    }
    public void setEstudiante(Estudiante estudiante){
        this.estudiante = estudiante;
    }
}

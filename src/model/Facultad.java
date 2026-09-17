package model;
import java.util.ArrayList;
import java.util.List;

public class Facultad {
    private String nombre;
    private List<Departamento> departamentos; // composicion
    private List<Carrera> carreras; // agregacion

    public Facultad(String nombre){
        this.nombre = nombre;
        this.departamentos = new ArrayList<>();
        this.carreras = new ArrayList<>();
    }
    // metodo composicion
    public void agregarDepartamento(String nombreDepartamento){
        Departamento depto = new Departamento(nombreDepartamento);
        this.departamentos.add(depto);
    }
    // metodo agregacion
    public void agregarCarrera(Carrera carrera){
        if (carrera != null){
            this.carreras.add(carrera);
        }
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public List<Departamento> getDepartamentos(){
        return departamentos;
    }

    public List<Carrera> getCarreras(){
        return carreras;
    }
}

package model;
import java.util.ArrayList;
import java.util.List;

public class Universidad {
    private String nombre;
    private int telefono;
    private List<Facultad> facultades;
    
    public Universidad(String nombre, int telefono){
        this.nombre = nombre;
        this.telefono = telefono;
        this.facultades = new ArrayList<>();
    }

    public void agregarFacultad(String nombreFacultad){
        Facultad facultad = new Facultad(nombreFacultad);
        this.facultades.add(facultad);
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public int getTelefono(){
        return telefono;
    }
    public void setTelefono(int telefono){
        this.telefono = telefono;
    }

    public List<Facultad> getFacultades(){
        return facultades;
    }
}

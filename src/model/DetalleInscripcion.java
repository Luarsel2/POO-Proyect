package model;
public class DetalleInscripcion {
    private int idDetalle;
    private String fechaInscripcion;
    private String estado;
    private Seccion seccion;

    public DetalleInscripcion(int idDetalle, String fechaInscripcion, String estado, Seccion seccion){
        this.idDetalle = idDetalle;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
        this.seccion = seccion;
    }

    public void asignarSeccion(){
        System.out.println("Detalle de la asignacion de seccion: ");
    }

    public void retirarSeccion(){
        System.out.println("Detalle del retiro de seccion: ");
    }

    public int getIdDetalle(){
        return idDetalle;
    }
    public void setIdDetalle(int idDetalle){
        this.idDetalle = idDetalle;
    }

    public String getFechaInscripcion(){
        return fechaInscripcion;
    }
    public void setFechaInscripcion(String fechaInscripcion){
        this.fechaInscripcion = fechaInscripcion;
    }
    
    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    public Seccion getSeccion(){
        return seccion;
    }
    public void setSeccion(Seccion seccion){
        this.seccion = seccion;
    }
}

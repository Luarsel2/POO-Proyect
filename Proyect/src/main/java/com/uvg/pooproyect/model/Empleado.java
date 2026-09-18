package com.uvg.pooproyect.model;

public class Empleado extends Persona {
    private int codigoDeEmpleado;
    private String fechaDeContratacion;

    public Empleado(int id, String nombre, String apellido, String fechaNacimiento, String correo, int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion){
        super(id, nombre,apellido, fechaNacimiento, correo, telefono, direccion);
        this.codigoDeEmpleado = codigoDeEmpleado;
        this.fechaDeContratacion = fechaDeContratacion;
    }

    public int getCodigoDeEmpleado(){
        return codigoDeEmpleado;
    }
    public void setCodigoDeEmpleado(int codigoDeEmpleado){
        this.codigoDeEmpleado = codigoDeEmpleado;
    }

    public String getFechaDeContratacion(){
        return fechaDeContratacion;
    }
    public void setFechaDeContratacion(String fechaDeContratacion){
        this.fechaDeContratacion = fechaDeContratacion;
    }

}

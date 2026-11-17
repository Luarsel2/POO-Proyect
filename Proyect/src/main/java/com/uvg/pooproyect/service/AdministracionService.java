package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.AdministracionRequest;
import com.uvg.pooproyect.model.Administracion;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class AdministracionService {
    private final AdministracionRepository repository;

    public AdministracionService(AdministracionRepository repository) {
        this.repository = repository;
    }

    public Map<String, String> registrar(AdministracionRequest req) {
        Administracion nuevo = new Administracion(
                req.id(), req.nombre(), req.apellido(), req.fechaNacimiento(), req.correo(),
                req.telefono(), req.direccion(), req.codigoDeEmpleado(), req.fechaDeContratacion(),
                req.puesto(), req.departamento()
        );
        repository.agregar(nuevo);
        repository.sincronizar();
        return Map.of("mensaje", "Empleado administrativo " + req.nombre() + " " + req.apellido()
                + " registrado con código " + req.codigoDeEmpleado());
    }

    public List<Administracion> obtenerTodos() {
        return repository.obtenerTodos();
    }

    public Administracion buscarPorCodigo(int codigo) {
        return repository.buscarPorCodigo(codigo);
    }
}

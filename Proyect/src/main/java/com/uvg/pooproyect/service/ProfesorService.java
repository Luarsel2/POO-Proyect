package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.ProfesorRequest;
import com.uvg.pooproyect.model.Profesor;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class ProfesorService {
    private final ProfesorRepository repository;

    public ProfesorService(ProfesorRepository repository) {
        this.repository = repository;
    }

    public Map<String, String> registrarProfesor(ProfesorRequest req) {
        Profesor nuevo = new Profesor(
                req.id(), req.nombre(), req.apellido(), req.fechaNacimiento(), req.correo(),
                req.telefono(), req.direccion(), req.codigoDeEmpleado(), req.fechaDeContratacion(),
                req.especialidad(), req.materias()
        );
        repository.agregar(nuevo);
        repository.sincronizar();
        return Map.of("mensaje", "Profesor " + req.nombre() + " " + req.apellido()
                + " registrado con código " + req.codigoDeEmpleado());
    }

    public List<Profesor> obtenerTodos() {
        return repository.obtenerTodos();
    }

    public Profesor buscarPorCodigo(int codigo) {
        return repository.buscarPorCodigo(codigo);
    }
}

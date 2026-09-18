package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.ProfesorRequest;
import com.uvg.pooproyect.model.Profesor;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    private static final String ARCHIVO = "profesores.csv";

    private final List<Profesor> listaProfesores = new ArrayList<>();

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Profesor p = new Profesor(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4],
                    CsvUtil.leerEntero(col[5], 0), col[6], CsvUtil.leerEntero(col[7], 0),
                    col[8], col[9], col[10], null
            );
            listaProfesores.add(p);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Profesor p : listaProfesores) {
            filas.add(new String[]{
                    String.valueOf(p.getId()), CsvUtil.escapar(p.getNombre()), CsvUtil.escapar(p.getApellido()),
                    CsvUtil.escapar(p.getFechaNacimiento()), CsvUtil.escapar(p.getCorreo()),
                    String.valueOf(p.getTelefono()), CsvUtil.escapar(p.getDireccion()),
                    String.valueOf(p.getCodigoDeEmpleado()), CsvUtil.escapar(p.getFechaDeContratacion()),
                    CsvUtil.escapar(p.getEspecialidad()), CsvUtil.escapar(p.getMaterias())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> registrarProfesor(@RequestBody ProfesorRequest req) {
        Profesor nuevo = new Profesor(
                req.id(), req.nombre(), req.apellido(), req.fechaNacimiento(), req.correo(),
                req.telefono(), req.direccion(), req.codigoDeEmpleado(), req.fechaDeContratacion(),
                req.especialidad(), req.materias(), null
        );
        listaProfesores.add(nuevo);
        guardar();
        return Map.of("mensaje", "Profesor " + req.nombre() + " " + req.apellido()
                + " registrado con código " + req.codigoDeEmpleado());
    }

    @GetMapping
    public List<Profesor> obtenerTodos() {
        return listaProfesores;
    }

    // usado internamente por SeccionController para asignar profesor a una seccion
    public Profesor buscarPorCodigo(int codigo) {
        for (Profesor p : listaProfesores) {
            if (p.getCodigoDeEmpleado() == codigo) {
                return p;
            }
        }
        return null;
    }
}

package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.AdministracionRequest;
import com.uvg.pooproyect.model.Administracion;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/administracion")
public class AdministracionController {

    private static final String ARCHIVO = "administracion.csv";

    private final List<Administracion> listaAdministrativos = new ArrayList<>();

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Administracion a = new Administracion(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4],
                    CsvUtil.leerEntero(col[5], 0), col[6], CsvUtil.leerEntero(col[7], 0),
                    col[8], col[9], col[10]
            );
            listaAdministrativos.add(a);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Administracion a : listaAdministrativos) {
            filas.add(new String[]{
                    String.valueOf(a.getId()), CsvUtil.escapar(a.getNombre()), CsvUtil.escapar(a.getApellido()),
                    CsvUtil.escapar(a.getFechaNacimiento()), CsvUtil.escapar(a.getCorreo()),
                    String.valueOf(a.getTelefono()), CsvUtil.escapar(a.getDireccion()),
                    String.valueOf(a.getCodigoDeEmpleado()), CsvUtil.escapar(a.getFechaDeContratacion()),
                    CsvUtil.escapar(a.getPuesto()), CsvUtil.escapar(a.getDepartamento())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> registrar(@RequestBody AdministracionRequest req) {
        Administracion nuevo = new Administracion(
                req.id(), req.nombre(), req.apellido(), req.fechaNacimiento(), req.correo(),
                req.telefono(), req.direccion(), req.codigoDeEmpleado(), req.fechaDeContratacion(),
                req.puesto(), req.departamento()
        );
        listaAdministrativos.add(nuevo);
        guardar();
        return Map.of("mensaje", "Empleado administrativo " + req.nombre() + " " + req.apellido()
                + " registrado con código " + req.codigoDeEmpleado());
    }

    @GetMapping
    public List<Administracion> obtenerTodos() {
        return listaAdministrativos;
    }
}

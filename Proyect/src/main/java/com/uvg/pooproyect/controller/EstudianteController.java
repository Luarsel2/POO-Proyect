package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.EstudianteRequest;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Version REST del EstudianteController original. La logica es la misma,
// solo que ahora cada metodo queda expuesto como un endpoint HTTP que el
// portal puede llamar, y la lista se guarda en data/estudiantes.csv para
// que los datos sobrevivan a un reinicio del servidor.
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private static final String ARCHIVO = "estudiantes.csv";

    private final List<Estudiante> listaEstudiantes = new ArrayList<>();

    // Se ejecuta una sola vez, cuando arranca el servidor: recupera lo que
    // ya se había guardado en el archivo .csv en una corrida anterior.
    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Estudiante e = new Estudiante(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4],
                    CsvUtil.leerEntero(col[5], 0), col[6], CsvUtil.leerEntero(col[7], 0),
                    CsvUtil.leerEntero(col[8], 0), col[9], CsvUtil.leerDecimal(col[10], 0),
                    null, null, CsvUtil.leerEntero(col[11], 0)
            );
            listaEstudiantes.add(e);
        }
    }

    // Reescribe el archivo .csv completo con lo que hay actualmente en memoria.
    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Estudiante e : listaEstudiantes) {
            filas.add(new String[]{
                    String.valueOf(e.getId()), CsvUtil.escapar(e.getNombre()), CsvUtil.escapar(e.getApellido()),
                    CsvUtil.escapar(e.getFechaNacimiento()), CsvUtil.escapar(e.getCorreo()),
                    String.valueOf(e.getTelefono()), CsvUtil.escapar(e.getDireccion()),
                    String.valueOf(e.getCarnet()), String.valueOf(e.getFechaIngreso()),
                    CsvUtil.escapar(e.getEstado()), String.valueOf(e.getPromedioGeneral()),
                    String.valueOf(e.getHistorialAcademico().getIdHistorial())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    // Registrar estudiante interactuando directamente con el modelo
    @PostMapping
    public Map<String, String> registrarEstudiante(@RequestBody EstudianteRequest req) {

        Estudiante nuevoEstudiante = new Estudiante(
                req.id(), req.nombre(), req.apellido(), req.fechaNacimiento(), req.correo(),
                req.telefono(), req.direccion(), req.carnet(), req.fechaIngreso(), req.estado(),
                req.promedioGeneral(), null, null, req.idHistorial()
        );

        listaEstudiantes.add(nuevoEstudiante);
        guardar();
        return Map.of("mensaje", "Estudiante " + req.nombre() + " " + req.apellido()
                + " registrado exitosamente. Carnet: " + req.carnet());
    }

    // Ejecutar lógica de negocio del modelo
    @PostMapping("/{carnet}/inscribir-curso")
    public Map<String, String> inscribirCursoAEstudiante(@PathVariable int carnet) {
        Estudiante estudiante = buscarPorCarnetInterno(carnet);

        if (estudiante != null) {
            estudiante.inscribirCurso();
            return Map.of("mensaje", "Curso inscrito exitosamente para el carnet: " + carnet);
        }

        return Map.of("mensaje", "Error: Estudiante con carnet " + carnet + " no encontrado.");
    }

    // Buscar estudiante por carnet
    @GetMapping("/{carnet}")
    public Estudiante buscarPorCarnet(@PathVariable int carnet) {
        return buscarPorCarnetInterno(carnet);
    }

    private Estudiante buscarPorCarnetInterno(int carnet) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getCarnet() == carnet) {
                return e;
            }
        }
        return null;
    }

    @GetMapping
    public List<Estudiante> obtenerTodos() {
        return listaEstudiantes;
    }
}

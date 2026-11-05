package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.DetalleInscripcion;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.model.Inscripcion;
import com.uvg.pooproyect.model.PeriodoAcademico;
import com.uvg.pooproyect.model.Seccion;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvInscripcionRepository implements InscripcionRepository {
    private static final String ARCHIVO = "inscripciones.csv";
    private final List<Inscripcion> listaInscripciones = new ArrayList<>();
    private final EstudianteRepository estudianteRepository;
    private final PeriodoRepository periodoRepository;
    private final SeccionRepository seccionRepository;
    public CsvInscripcionRepository(EstudianteRepository estudianteRepository, PeriodoRepository periodoRepository, SeccionRepository seccionRepository) {
        this.estudianteRepository = estudianteRepository;
        this.periodoRepository = periodoRepository;
        this.seccionRepository = seccionRepository;
    }

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Estudiante estudiante = estudianteRepository.buscarPorCarnet(CsvUtil.leerEntero(col[4], -1));
            PeriodoAcademico periodo = periodoRepository.buscarPorId(CsvUtil.leerEntero(col[5], -1));
            if (estudiante == null || periodo == null) {
                // El estudiante o el período ya no existen; se omite esta inscripción guardada.
                continue;
            }
            Inscripcion inscripcion = new Inscripcion(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], CsvUtil.leerEntero(col[3], 0),
                    estudiante, periodo
            );
            if (col.length > 6 && !col[6].isBlank()) {
                for (String bloque : col[6].split(";")) {
                    String[] campos = bloque.split(":", -1);
                    if (campos.length != 4) continue;
                    Seccion seccion = seccionRepository.buscarPorId(CsvUtil.leerEntero(campos[3], -1));
                    if (seccion != null) {
                        inscripcion.agregarDetalle(CsvUtil.leerEntero(campos[0], 0), campos[1], campos[2], seccion);
                    }
                }
            }
            listaInscripciones.add(inscripcion);
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Inscripcion i : listaInscripciones) {
            StringBuilder detallesEncoded = new StringBuilder();
            for (DetalleInscripcion d : i.getDetalles()) {
                if (detallesEncoded.length() > 0) detallesEncoded.append(";");
                detallesEncoded.append(d.getIdDetalle()).append(":").append(d.getFechaInscripcion())
                        .append(":").append(d.getEstado()).append(":").append(d.getSeccion().getIdSeccion());
            }
            filas.add(new String[]{
                    String.valueOf(i.getId()), CsvUtil.escapar(i.getFechaDeInscripcion()),
                    CsvUtil.escapar(i.getEstado()), String.valueOf(i.getTotalCreditos()),
                    String.valueOf(i.getEstudiante().getCarnet()),
                    String.valueOf(i.getPeriodoAcademico().getIdPeriodo()),
                    detallesEncoded.toString()
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Inscripcion> obtenerTodos() { return List.copyOf(listaInscripciones); }

    @Override
    public void agregar(Inscripcion entidad) { listaInscripciones.add(entidad); }

    @Override
    public Inscripcion buscarPorId(int id) {
        for (Inscripcion i : listaInscripciones) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }
}

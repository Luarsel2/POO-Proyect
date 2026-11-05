package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Horario;
import com.uvg.pooproyect.model.Profesor;
import com.uvg.pooproyect.model.Seccion;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvSeccionRepository implements SeccionRepository {
    private static final String ARCHIVO = "secciones.csv";
    private final List<Seccion> listaSecciones = new ArrayList<>();
    private final ProfesorRepository profesorRepository;
    public CsvSeccionRepository(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Profesor profesor = profesorRepository.buscarPorCodigo(CsvUtil.leerEntero(col[5], -1));
            Seccion seccion = new Seccion(
                    CsvUtil.leerEntero(col[0], 0), CsvUtil.leerEntero(col[1], 0),
                    CsvUtil.leerEntero(col[2], 0), CsvUtil.leerEntero(col[3], 0),
                    col[4], profesor, null
            );
            if (col.length > 6 && !col[6].isBlank()) {
                for (String bloque : col[6].split(";")) {
                    String[] campos = bloque.split(":", -1);
                    if (campos.length == 4) {
                        seccion.agregarHorario(CsvUtil.leerEntero(campos[0], 0), campos[1], campos[2], campos[3]);
                    } else if (campos.length == 6) {
                        // Las horas HH:mm también contienen el separador del formato histórico.
                        seccion.agregarHorario(CsvUtil.leerEntero(campos[0], 0), campos[1],
                                campos[2] + ":" + campos[3], campos[4] + ":" + campos[5]);
                    }
                }
            }
            listaSecciones.add(seccion);
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Seccion s : listaSecciones) {
            StringBuilder horariosEncoded = new StringBuilder();
            for (Horario h : s.getHorario()) {
                if (horariosEncoded.length() > 0) horariosEncoded.append(";");
                horariosEncoded.append(h.getIdHorario()).append(":").append(h.getDiaSemana())
                        .append(":").append(h.getHoraInicio()).append(":").append(h.getHoraFin());
            }
            filas.add(new String[]{
                    String.valueOf(s.getIdSeccion()), String.valueOf(s.getCodigo()),
                    String.valueOf(s.getCupoMaximo()), String.valueOf(s.getCupoActual()),
                    CsvUtil.escapar(s.getEstado()),
                    s.getProfesor() != null ? String.valueOf(s.getProfesor().getCodigoDeEmpleado()) : "",
                    horariosEncoded.toString()
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Seccion> obtenerTodos() { return List.copyOf(listaSecciones); }

    @Override
    public void agregar(Seccion entidad) { listaSecciones.add(entidad); }

    @Override
    public Seccion buscarPorId(int idSeccion) {
        for (Seccion s : listaSecciones) {
            if (s.getIdSeccion() == idSeccion) {
                return s;
            }
        }
        return null;
    }
}

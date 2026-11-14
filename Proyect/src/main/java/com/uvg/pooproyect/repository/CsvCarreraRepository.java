package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Carrera;
import com.uvg.pooproyect.model.Curso;
import com.uvg.pooproyect.model.PlanDeEstudio;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvCarreraRepository implements CarreraRepository {
    private static final String ARCHIVO = "carreras.csv";
    private final List<Carrera> listaCarreras = new ArrayList<>();
    private final CursoRepository cursoRepository;
    public CsvCarreraRepository(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Carrera carrera = new Carrera(col[0], col[1], CsvUtil.leerEntero(col[2], 0));
            if (col.length > 3 && !col[3].isBlank()) {
                for (String bloquePlan : col[3].split(";")) {
                    String[] campos = bloquePlan.split(":", -1);
                    if (campos.length < 2) continue;

                    int year = CsvUtil.leerEntero(campos[0], 0);
                    String estado = campos[1];
                    carrera.agregarPlanDeEstudio(year, estado);

                    if (campos.length > 2 && !campos[2].isBlank()) {
                        PlanDeEstudio plan = carrera.getPlanesDeEstudios().get(carrera.getPlanesDeEstudios().size() - 1);
                        for (String nombreCurso : campos[2].split("\\|")) {
                            Curso curso = cursoRepository.buscarPorNombre(nombreCurso);
                            if (curso != null) {
                                plan.agregarCurso(curso);
                            }
                        }
                    }
                }
            }
            listaCarreras.add(carrera);
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Carrera c : listaCarreras) {
            String planesEncoded = c.getPlanesDeEstudios().stream()
                    .map(p -> {
                        String cursosEncoded = p.getCursos().stream()
                                .map(Curso::getNombre)
                                .collect(Collectors.joining("|"));
                        return p.getYear() + ":" + p.getEstado() + ":" + cursosEncoded;
                    })
                    .collect(Collectors.joining(";"));

            filas.add(new String[]{
                    CsvUtil.escapar(c.getNombre()),
                    CsvUtil.escapar(c.getTotalCreditos()),
                    String.valueOf(c.getDuracionPromedio()),
                    planesEncoded
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Carrera> obtenerTodos() { return List.copyOf(listaCarreras); }

    @Override
    public void agregar(Carrera entidad) { listaCarreras.add(entidad); }

    @Override
    public Carrera buscarPorNombre(String nombre) {
        return listaCarreras.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
}

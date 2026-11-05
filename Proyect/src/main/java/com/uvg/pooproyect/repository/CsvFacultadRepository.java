package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Carrera;
import com.uvg.pooproyect.model.Departamento;
import com.uvg.pooproyect.model.Facultad;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvFacultadRepository implements FacultadRepository {
    private static final String ARCHIVO = "facultades.csv";
    private final List<Facultad> listaFacultades = new ArrayList<>();
    private final CarreraRepository carreraRepository;
    public CsvFacultadRepository(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Facultad facultad = new Facultad(col[0]);
            if (col.length > 1 && !col[1].isBlank()) {
                for (String depto : col[1].split(";")) {
                    facultad.agregarDepartamento(depto);
                }
            }
            if (col.length > 2 && !col[2].isBlank()) {
                for (String nombreCarrera : col[2].split(";")) {
                    Carrera carrera = carreraRepository.buscarPorNombre(nombreCarrera);
                    if (carrera != null) {
                        facultad.agregarCarrera(carrera);
                    }
                }
            }
            listaFacultades.add(facultad);
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Facultad f : listaFacultades) {
            StringBuilder deptos = new StringBuilder();
            for (Departamento d : f.getDepartamentos()) {
                if (deptos.length() > 0) deptos.append(";");
                deptos.append(d.getNombre());
            }
            StringBuilder carreras = new StringBuilder();
            for (Carrera c : f.getCarreras()) {
                if (carreras.length() > 0) carreras.append(";");
                carreras.append(c.getNombre());
            }
            filas.add(new String[]{CsvUtil.escapar(f.getNombre()), deptos.toString(), carreras.toString()});
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Facultad> obtenerTodos() { return List.copyOf(listaFacultades); }

    @Override
    public void agregar(Facultad entidad) { listaFacultades.add(entidad); }

    @Override
    public Facultad buscarPorNombre(String nombre) {
        for (Facultad f : listaFacultades) {
            if (f.getNombre().equalsIgnoreCase(nombre)) {
                return f;
            }
        }
        return null;
    }
}

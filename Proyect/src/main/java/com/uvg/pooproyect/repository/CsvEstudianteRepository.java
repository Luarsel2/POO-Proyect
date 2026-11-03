package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CsvEstudianteRepository implements EstudianteRepository {

    private static final String ARCHIVO = "estudiantes.csv";
    private static final String ARCHIVO_ASIGNACIONES = "asignaciones.csv";

    private final List<Estudiante> listaEstudiantes = new ArrayList<>();
    private final Map<Integer, List<Integer>> asignacionesPorCarnet = new LinkedHashMap<>();
    private final List<SeccionCatalogo> catalogoSecciones = new ArrayList<>();

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
        inicializarCatalogo();
        cargarAsignaciones();
    }

    private void inicializarCatalogo() {
        catalogoSecciones.clear();

        catalogoSecciones.add(new SeccionCatalogo(
                101, 10, "CC2008", "Programación Orientada a Objetos", 4,
                "Ing. Carlos Mendoza", "Lun, Mié 07:00 - 08:30", "Matutina", "CIT-401", "#0284c7", 30, 18,
                List.of(
                        new BloqueHorario("Lunes", "07:00", "08:30", "CIT-401"),
                        new BloqueHorario("Miércoles", "07:00", "08:30", "CIT-401")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                102, 10, "CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4,
                "Ing. Alejandro Morales", "Mar, Jue 07:00 - 08:30", "Matutina", "CIT-302", "#059669", 28, 14,
                List.of(
                        new BloqueHorario("Martes", "07:00", "08:30", "CIT-302"),
                        new BloqueHorario("Jueves", "07:00", "08:30", "CIT-302")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                103, 10, "MA1004", "Cálculo I", 4,
                "Dr. Roberto Chang", "Lun, Mié 08:45 - 10:15", "Matutina", "CIT-204", "#7c3aed", 35, 20,
                List.of(
                        new BloqueHorario("Lunes", "08:45", "10:15", "CIT-204"),
                        new BloqueHorario("Miércoles", "08:45", "10:15", "CIT-204")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                104, 10, "MA2005", "Estadística y Probabilidad", 3,
                "Inga. Claudia Morales", "Mar, Jue 08:45 - 10:15", "Matutina", "CIT-104", "#ea580c", 30, 15,
                List.of(
                        new BloqueHorario("Martes", "08:45", "10:15", "CIT-104"),
                        new BloqueHorario("Jueves", "08:45", "10:15", "CIT-104")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                105, 10, "SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3,
                "Dr. Fernando Morales", "Lun, Mié 10:40 - 12:00", "Matutina", "A-101", "#db2777", 30, 12,
                List.of(
                        new BloqueHorario("Lunes", "10:40", "12:00", "A-101"),
                        new BloqueHorario("Miércoles", "10:40", "12:00", "A-101")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                106, 10, "SH1001", "Guatemala en el Contexto Global", 3,
                "Inga. Sofía Castillo", "Mar, Jue 10:40 - 12:00 y Vie 10:40 - 13:00", "Matutina", "CIT-201", "#0d9488", 32, 16,
                List.of(
                        new BloqueHorario("Martes", "10:40", "12:00", "CIT-201"),
                        new BloqueHorario("Jueves", "10:40", "12:00", "CIT-201"),
                        new BloqueHorario("Viernes", "10:40", "13:00", "CIT-201")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                201, 30, "CC2008", "Programación Orientada a Objetos", 4,
                "Ing. Rodrigo Palma", "Lun, Mié 15:00 - 17:00", "Nocturna", "CIT-401", "#0284c7", 30, 10,
                List.of(
                        new BloqueHorario("Lunes", "15:00", "17:00", "CIT-401"),
                        new BloqueHorario("Miércoles", "15:00", "17:00", "CIT-401")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                202, 30, "CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4,
                "Inga. María Inés Ortiz", "Mar, Jue 15:00 - 17:00", "Nocturna", "CIT-302", "#059669", 28, 12,
                List.of(
                        new BloqueHorario("Martes", "15:00", "17:00", "CIT-302"),
                        new BloqueHorario("Jueves", "15:00", "17:00", "CIT-302")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                203, 30, "MA1004", "Cálculo I", 4,
                "Lic. Mario Estrada", "Lun, Mié 17:15 - 19:00", "Nocturna", "CIT-204", "#7c3aed", 35, 14,
                List.of(
                        new BloqueHorario("Lunes", "17:15", "19:00", "CIT-204"),
                        new BloqueHorario("Miércoles", "17:15", "19:00", "CIT-204")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                204, 30, "MA2005", "Estadística y Probabilidad", 3,
                "Licda. Karen Pinto", "Mar, Jue 17:15 - 19:00", "Nocturna", "CIT-104", "#ea580c", 30, 11,
                List.of(
                        new BloqueHorario("Martes", "17:15", "19:00", "CIT-104"),
                        new BloqueHorario("Jueves", "17:15", "19:00", "CIT-104")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                205, 30, "SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3,
                "Dr. Víctor Arriola", "Lun, Mié 19:15 - 21:00", "Nocturna", "A-101", "#db2777", 30, 16,
                List.of(
                        new BloqueHorario("Lunes", "19:15", "21:00", "A-101"),
                        new BloqueHorario("Miércoles", "19:15", "21:00", "A-101")
                )
        ));

        catalogoSecciones.add(new SeccionCatalogo(
                206, 30, "SH1001", "Guatemala en el Contexto Global", 3,
                "Lic. Guillermo Fuentes", "Mar, Jue 19:15 - 21:00 y Vie 18:00 - 21:00", "Nocturna", "CIT-201", "#0d9488", 32, 13,
                List.of(
                        new BloqueHorario("Martes", "19:15", "21:00", "CIT-201"),
                        new BloqueHorario("Jueves", "19:15", "21:00", "CIT-201"),
                        new BloqueHorario("Viernes", "18:00", "21:00", "CIT-201")
                )
        ));
    }

    private void cargarAsignaciones() {
        asignacionesPorCarnet.clear();
        List<String[]> filas = CsvUtil.leerFilas(ARCHIVO_ASIGNACIONES);
        if (filas.isEmpty()) {
            asignacionesPorCarnet.put(26547, new ArrayList<>());
            guardarAsignacionesEnCsv();
            return;
        }
        for (String[] col : filas) {
            if (col.length >= 2) {
                int carnet = CsvUtil.leerEntero(col[0], 0);
                int idSec = CsvUtil.leerEntero(col[1], 0);
                if (carnet > 0 && idSec > 0 && buscarSeccionCatalogo(idSec) != null) {
                    asignacionesPorCarnet.computeIfAbsent(carnet, k -> new ArrayList<>()).add(idSec);
                }
            }
        }
    }

    private void guardarAsignacionesEnCsv() {
        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : asignacionesPorCarnet.entrySet()) {
            for (Integer idSec : entry.getValue()) {
                filas.add(new String[]{String.valueOf(entry.getKey()), String.valueOf(idSec)});
            }
        }
        CsvUtil.escribirFilas(ARCHIVO_ASIGNACIONES, filas);
    }

    private void guardarEnCsv() {
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

    @Override
    public List<Estudiante> obtenerTodos() {
        return listaEstudiantes;
    }

    @Override
    public Estudiante buscarPorCarnet(int carnet) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getCarnet() == carnet) {
                return e;
            }
        }
        if (carnet == 26547) {
            Estudiante fallback = new Estudiante(26547, "Luis", "Garcia", "2004-09-18", "prueba26547@uvg.edu.gt",
                    52147890, "Calzada Roosevelt Zona 11", 26547, 2025, "Activo", 82.5, null, null, 26547);
            listaEstudiantes.add(fallback);
            return fallback;
        }
        if (carnet == 26710) {
            Estudiante fallback = new Estudiante(26710, "Gabriel", "Yanes", "2006-03-22", "prueba26710@uvg.edu.gt",
                    41258963, "Carretera a El Salvador", 26710, 2026, "Activo", 71.0, null, null, 26710);
            listaEstudiantes.add(fallback);
            return fallback;
        }
        if (carnet == 26000) {
            Estudiante fallback = new Estudiante(444, "Pedro", "Garcia", "2006-01-11", "pedro@gmail.com",
                    33334444, "Vista Hermosa Zona 15", 26000, 2026, "Activo", 95.0, null, null, 89890);
            listaEstudiantes.add(fallback);
            return fallback;
        }
        Estudiante fallback = new Estudiante(carnet, "Estudiante", "UVG", "2000-01-01", "estudiante" + carnet + "@uvg.edu.gt",
                0, "Guatemala", carnet, 2026, "Activo", 85.0, null, null, carnet);
        listaEstudiantes.add(fallback);
        return fallback;
    }

    @Override
    public Estudiante guardar(Estudiante estudiante) {
        listaEstudiantes.add(estudiante);
        guardarEnCsv();
        return estudiante;
    }

    @Override
    public void sincronizarEstudiantes() {
        guardarEnCsv();
    }

    @Override
    public List<Integer> obtenerAsignacionesPorCarnet(int carnet) {
        return asignacionesPorCarnet.computeIfAbsent(carnet, k -> new ArrayList<>());
    }

    @Override
    public void guardarAsignaciones(int carnet, List<Integer> asignaciones) {
        asignacionesPorCarnet.put(carnet, asignaciones);
        guardarAsignacionesEnCsv();
    }

    @Override
    public List<SeccionCatalogo> obtenerCatalogoSecciones() {
        return catalogoSecciones;
    }

    @Override
    public SeccionCatalogo buscarSeccionCatalogo(int idSeccion) {
        for (SeccionCatalogo s : catalogoSecciones) {
            if (s.idSeccion == idSeccion) {
                return s;
            }
        }
        return null;
    }
}

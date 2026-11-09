package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.EstudianteRequest;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// Version REST del EstudianteController original. La logica es la misma,
// solo que ahora cada metodo queda expuesto como un endpoint HTTP que el
// portal puede llamar, y la lista se guarda en data/estudiantes.csv para
// que los datos sobrevivan a un reinicio del servidor.
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private static final String ARCHIVO = "estudiantes.csv";
    private static final String ARCHIVO_ASIGNACIONES = "asignaciones.csv";

    private final List<Estudiante> listaEstudiantes = new ArrayList<>();
    private final Map<Integer, List<Integer>> asignacionesPorCarnet = new LinkedHashMap<>();
    private final List<SeccionCatalogo> catalogoSecciones = new ArrayList<>();

    public record BloqueHorario(String dia, String horaInicio, String horaFin, String salon) {}

    public static class SeccionCatalogo {
        public int idSeccion;
        public int codigoSeccion;
        public String codigoCurso;
        public String nombreCurso;
        public int creditos;
        public String profesor;
        public String horarios;
        public String jornada;
        public String salon;
        public String color;
        public int cupoMaximo;
        public int cupoActual;
        public List<BloqueHorario> bloques;

        public SeccionCatalogo(int idSeccion, int codigoSeccion, String codigoCurso, String nombreCurso, int creditos,
                               String profesor, String horarios, String jornada, String salon, String color,
                               int cupoMaximo, int cupoActual, List<BloqueHorario> bloques) {
            this.idSeccion = idSeccion;
            this.codigoSeccion = codigoSeccion;
            this.codigoCurso = codigoCurso;
            this.nombreCurso = nombreCurso;
            this.creditos = creditos;
            this.profesor = profesor;
            this.horarios = horarios;
            this.jornada = jornada;
            this.salon = salon;
            this.color = color;
            this.cupoMaximo = cupoMaximo;
            this.cupoActual = cupoActual;
            this.bloques = bloques;
        }
    }

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
            guardarAsignaciones();
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

    private void guardarAsignaciones() {
        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : asignacionesPorCarnet.entrySet()) {
            for (Integer idSec : entry.getValue()) {
                filas.add(new String[]{String.valueOf(entry.getKey()), String.valueOf(idSec)});
            }
        }
        CsvUtil.escribirFilas(ARCHIVO_ASIGNACIONES, filas);
    }

    private SeccionCatalogo buscarSeccionCatalogo(int idSeccion) {
        for (SeccionCatalogo s : catalogoSecciones) {
            if (s.idSeccion == idSeccion) {
                return s;
            }
        }
        return null;
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
    public Map<String, String> inscribirCursoAEstudiante(@PathVariable("carnet") int carnet) {
        Estudiante estudiante = buscarPorCarnetInterno(carnet);

        if (estudiante != null) {
            estudiante.inscribirCurso();
            return Map.of("mensaje", "Curso inscrito exitosamente para el carnet: " + carnet);
        }

        return Map.of("mensaje", "Error: Estudiante con carnet " + carnet + " no encontrado.");
    }

    // Buscar estudiante por carnet
    @GetMapping("/{carnet}")
    public Estudiante buscarPorCarnet(@PathVariable("carnet") int carnet) {
        return buscarPorCarnetInterno(carnet);
    }

    private Estudiante buscarPorCarnetInterno(int carnet) {
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

    private int resolverCarnet(Integer carnet, HttpSession sesion) {
        if (carnet != null && carnet > 0) {
            return carnet;
        }
        if (sesion != null) {
            Object sc = sesion.getAttribute("carnet");
            if (sc != null) {
                try {
                    return Integer.parseInt(sc.toString());
                } catch (Exception ignored) {}
            }
        }
        return 26547;
    }

    @GetMapping
    public List<Estudiante> obtenerTodos() {
        return listaEstudiantes;
    }

    @GetMapping({"/dashboard", "/{carnet}/dashboard"})
    public ResponseEntity<Map<String, Object>> obtenerDashboardEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.notFound().build();
        }

        List<Integer> idsAsignados = asignacionesPorCarnet.getOrDefault(carnetFinal, Collections.emptyList());
        List<Map<String, Object>> cursosActuales = new ArrayList<>();
        int totalCreditos = 0;

        for (Integer idSec : idsAsignados) {
            SeccionCatalogo sec = buscarSeccionCatalogo(idSec);
            if (sec != null) {
                totalCreditos += sec.creditos;
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("idSeccion", sec.idSeccion);
                map.put("codigoCurso", sec.codigoCurso);
                map.put("nombreCurso", sec.nombreCurso);
                map.put("codigoSeccion", sec.codigoSeccion);
                map.put("profesor", sec.profesor);
                map.put("horarios", sec.horarios);
                map.put("jornada", sec.jornada);
                map.put("salon", sec.salon);
                map.put("creditos", sec.creditos);
                map.put("color", sec.color);
                map.put("estado", "Inscrito");
                cursosActuales.add(map);
            }
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("nombre", est.getNombre() + " " + est.getApellido());
        resp.put("carrera", "Ingeniería en Ciencias de la Computación");
        resp.put("facultad", "Facultad de Ingeniería");
        resp.put("periodoActual", "Segundo Semestre 2026");
        resp.put("cantidadCursos", cursosActuales.size());
        resp.put("totalCreditos", totalCreditos);
        resp.put("cursosActuales", cursosActuales);

        return ResponseEntity.ok(resp);
    }

    @GetMapping({"/cursos-disponibles", "/{carnet}/cursos-disponibles"})
    public ResponseEntity<Map<String, Object>> obtenerCursosDisponibles(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        List<Integer> idsAsignados = asignacionesPorCarnet.getOrDefault(carnetFinal, Collections.emptyList());
        Map<String, List<SeccionCatalogo>> porCurso = new LinkedHashMap<>();
        for (SeccionCatalogo sec : catalogoSecciones) {
            porCurso.computeIfAbsent(sec.codigoCurso, k -> new ArrayList<>()).add(sec);
        }

        List<Map<String, Object>> cursosResp = new ArrayList<>();
        for (Map.Entry<String, List<SeccionCatalogo>> entry : porCurso.entrySet()) {
            List<SeccionCatalogo> secs = entry.getValue();
            SeccionCatalogo first = secs.get(0);

            List<Map<String, Object>> seccionesList = new ArrayList<>();
            for (SeccionCatalogo s : secs) {
                Map<String, Object> sm = new LinkedHashMap<>();
                sm.put("idSeccion", s.idSeccion);
                sm.put("codigo", s.codigoSeccion);
                sm.put("jornada", s.jornada);
                sm.put("profesor", s.profesor);
                sm.put("horarios", s.horarios);
                sm.put("salon", s.salon);
                sm.put("color", s.color);
                sm.put("cupoMaximo", s.cupoMaximo);
                sm.put("cupoActual", s.cupoActual);
                sm.put("asignada", idsAsignados.contains(s.idSeccion));
                seccionesList.add(sm);
            }

            Map<String, Object> cursoMap = new LinkedHashMap<>();
            cursoMap.put("codigo", first.codigoCurso);
            cursoMap.put("nombre", first.nombreCurso);
            cursoMap.put("creditos", first.creditos);
            cursoMap.put("carrera", "Ingeniería en Ciencias de la Computación");
            cursoMap.put("secciones", seccionesList);
            cursosResp.add(cursoMap);
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("periodo", "Segundo Semestre 2026");
        resp.put("cursos", cursosResp);

        return ResponseEntity.ok(resp);
    }

    @PostMapping({"/asignar-curso", "/{carnet}/asignar-curso"})
    public ResponseEntity<Map<String, Object>> asignarCurso(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> req,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Estudiante no encontrado."));
        }

        Object idObj = req.get("idSeccion");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Debe especificar idSeccion."));
        }
        int idSeccion;
        if (idObj instanceof Number n) {
            idSeccion = n.intValue();
        } else {
            try {
                idSeccion = Integer.parseInt(idObj.toString());
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Identificador de sección inválido."));
            }
        }

        SeccionCatalogo nueva = buscarSeccionCatalogo(idSeccion);
        if (nueva == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Sección no encontrada."));
        }

        List<Integer> asignados = asignacionesPorCarnet.computeIfAbsent(carnetFinal, k -> new ArrayList<>());
        if (asignados.contains(idSeccion)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Ya estás inscrito en esta sección."));
        }

        for (Integer yaId : new ArrayList<>(asignados)) {
            SeccionCatalogo yaSec = buscarSeccionCatalogo(yaId);
            if (yaSec != null && yaSec.codigoCurso.equalsIgnoreCase(nueva.codigoCurso)) {
                asignados.remove(yaId);
                if (yaSec.cupoActual > 0) {
                    yaSec.cupoActual--;
                }
            }
        }

        if (nueva.cupoActual >= nueva.cupoMaximo) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "No hay cupos disponibles en esta sección."));
        }

        asignados.add(idSeccion);
        nueva.cupoActual++;
        guardarAsignaciones();
        est.inscribirCurso();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("mensaje", "Asignación exitosa: " + nueva.nombreCurso + " (Sección " + nueva.codigoSeccion + " - " + nueva.jornada + ")");
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/asignar-horario", "/{carnet}/asignar-horario"})
    public ResponseEntity<Map<String, Object>> asignarHorarioCompleto(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> req,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Estudiante no encontrado."));
        }

        String jornada = String.valueOf(req.getOrDefault("jornada", "Matutina")).trim();
        boolean esNocturna = jornada.equalsIgnoreCase("Nocturna");

        List<Integer> idsDeseados = esNocturna
                ? List.of(201, 202, 203, 204, 205, 206)
                : List.of(101, 102, 103, 104, 105, 106);

        List<Integer> asignados = asignacionesPorCarnet.computeIfAbsent(carnetFinal, k -> new ArrayList<>());
        for (Integer yaId : new ArrayList<>(asignados)) {
            SeccionCatalogo sec = buscarSeccionCatalogo(yaId);
            if (sec != null && sec.cupoActual > 0) {
                sec.cupoActual--;
            }
        }
        asignados.clear();

        for (Integer nuevoId : idsDeseados) {
            SeccionCatalogo sec = buscarSeccionCatalogo(nuevoId);
            if (sec != null) {
                asignados.add(nuevoId);
                sec.cupoActual++;
            }
        }

        guardarAsignaciones();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("jornada", esNocturna ? "Nocturna" : "Matutina");
        res.put("mensaje", "Horario " + (esNocturna ? "Nocturno (15:00 - 21:00)" : "Matutino (07:00 - 12:00 / Vie 13:00)") + " asignado exitosamente (6 asignaturas).");
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/retirar-todos", "/{carnet}/retirar-todos"})
    public ResponseEntity<Map<String, Object>> retirarTodosLosCursos(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Estudiante no encontrado."));
        }

        List<Integer> asignados = asignacionesPorCarnet.get(carnetFinal);
        if (asignados != null) {
            for (Integer idSec : asignados) {
                SeccionCatalogo sec = buscarSeccionCatalogo(idSec);
                if (sec != null && sec.cupoActual > 0) {
                    sec.cupoActual--;
                }
            }
            asignados.clear();
        }
        guardarAsignaciones();

        return ResponseEntity.ok(Map.of("ok", true, "mensaje", "Se han retirado todas las asignaturas correctamente."));
    }

    @PostMapping({"/retirar-curso", "/{carnet}/retirar-curso"})
    public ResponseEntity<Map<String, Object>> retirarCurso(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> req,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Estudiante no encontrado."));
        }

        Object idObj = req.get("idSeccion");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Debe especificar idSeccion."));
        }
        int idSeccion;
        if (idObj instanceof Number n) {
            idSeccion = n.intValue();
        } else {
            try {
                idSeccion = Integer.parseInt(idObj.toString());
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Identificador de sección inválido."));
            }
        }

        List<Integer> asignados = asignacionesPorCarnet.get(carnetFinal);
        if (asignados == null || !asignados.contains(idSeccion)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "El estudiante no tiene asignada esa sección."));
        }

        asignados.remove(Integer.valueOf(idSeccion));
        SeccionCatalogo sec = buscarSeccionCatalogo(idSeccion);
        if (sec != null && sec.cupoActual > 0) {
            sec.cupoActual--;
        }
        guardarAsignaciones();
        est.retirarCurso();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("mensaje", "Curso retirado exitosamente: " + (sec != null ? sec.nombreCurso : "Sección " + idSeccion));
        return ResponseEntity.ok(res);
    }

    @GetMapping({"/horario", "/{carnet}/horario"})
    public ResponseEntity<Map<String, Object>> obtenerHorarioEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        List<Integer> idsAsignados = asignacionesPorCarnet.getOrDefault(carnetFinal, Collections.emptyList());
        List<Map<String, Object>> bloquesResp = new ArrayList<>();

        for (Integer idSec : idsAsignados) {
            SeccionCatalogo sec = buscarSeccionCatalogo(idSec);
            if (sec != null && sec.bloques != null) {
                for (BloqueHorario b : sec.bloques) {
                    Map<String, Object> bm = new LinkedHashMap<>();
                    bm.put("idSeccion", sec.idSeccion);
                    bm.put("codigoCurso", sec.codigoCurso);
                    bm.put("nombreCurso", sec.nombreCurso);
                    bm.put("codigoSeccion", sec.codigoSeccion);
                    bm.put("profesor", sec.profesor);
                    bm.put("dia", b.dia());
                    bm.put("horaInicio", b.horaInicio());
                    bm.put("horaFin", b.horaFin());
                    bm.put("salon", b.salon());
                    bm.put("color", sec.color);
                    bm.put("jornada", sec.jornada);
                    bloquesResp.add(bm);
                }
            }
        }

        Map<String, Object> recesoMap = new LinkedHashMap<>();
        recesoMap.put("horaInicio", "10:15");
        recesoMap.put("horaFin", "10:40");
        recesoMap.put("descripcion", "Receso Académico UVG");
        recesoMap.put("dias", List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("periodo", "Segundo Semestre 2026");
        resp.put("receso", recesoMap);
        resp.put("bloques", bloquesResp);

        return ResponseEntity.ok(resp);
    }

    public record ActividadNota(String nombre, double ponderacion, double obtenido, String fecha, String retroalimentacion) {}

    public static class NotaCurso {
        public String codigoCurso;
        public String nombreCurso;
        public int creditos;
        public String seccion;
        public String profesor;
        public String color;
        public double zonaTotal;
        public double zonaObtenida;
        public double examenFinal;
        public double notaTotal;
        public String estado;
        public List<ActividadNota> desglose;

        public NotaCurso(String codigoCurso, String nombreCurso, int creditos, String seccion, String profesor,
                         String color, double zonaTotal, double zonaObtenida, double examenFinal,
                         double notaTotal, String estado, List<ActividadNota> desglose) {
            this.codigoCurso = codigoCurso;
            this.nombreCurso = nombreCurso;
            this.creditos = creditos;
            this.seccion = seccion;
            this.profesor = profesor;
            this.color = color;
            this.zonaTotal = zonaTotal;
            this.zonaObtenida = zonaObtenida;
            this.examenFinal = examenFinal;
            this.notaTotal = notaTotal;
            this.estado = estado;
            this.desglose = desglose;
        }
    }

    private List<NotaCurso> generarMockNotas(int carnet) {
        List<NotaCurso> list = new ArrayList<>();

        if (carnet == 26000) {
            list.add(new NotaCurso("CC2008", "Programación Orientada a Objetos", 4, "Sec. 10 (Matutina)", "Ing. Carlos Mendoza", "#0284c7",
                    75.0, 73.0, 0.0, 73.0, "En curso",
                    List.of(
                            new ActividadNota("Laboratorio 1: Modelado de Clases y Objetos", 5.0, 5.0, "18/07/2026", "Abstracción y modularidad impecable."),
                            new ActividadNota("Corto 1: Encapsulamiento y Constructores", 5.0, 5.0, "25/07/2026", "Excelente dominio conceptual."),
                            new ActividadNota("Laboratorio 2: Polimorfismo e Interfaces", 10.0, 10.0, "08/08/2026", "Diseño extensible y pruebas completas."),
                            new ActividadNota("Examen Parcial 1: Fundamentos POO", 20.0, 19.5, "22/08/2026", "Análisis sobresaliente de arquitectura."),
                            new ActividadNota("Laboratorio 3: Manejo de Excepciones y Colecciones", 10.0, 10.0, "05/09/2026", "Control de errores de nivel profesional."),
                            new ActividadNota("Proyecto Fase 1: Arquitectura de Software", 15.0, 14.5, "26/09/2026", "Patrones y documentación de excelencia."),
                            new ActividadNota("Examen Parcial 2: Patrones y Estructuras", 10.0, 9.0, "10/10/2026", "Aplicación perfecta de patrones."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4, "Sec. 10 (Matutina)", "Ing. Alejandro Morales", "#059669",
                    75.0, 72.0, 0.0, 72.0, "En curso",
                    List.of(
                            new ActividadNota("Práctica 1: Arquitectura de Registros x86-64", 5.0, 5.0, "20/07/2026", "Manejo óptimo de registros de 64 bits."),
                            new ActividadNota("Laboratorio 1: Aritmética y Banderas de Estado", 10.0, 10.0, "03/08/2026", "Instrucciones eficientes y compactas."),
                            new ActividadNota("Examen Parcial 1: Modos de Direccionamiento", 20.0, 19.5, "24/08/2026", "Manipulación precisa de la pila."),
                            new ActividadNota("Laboratorio 2: Manejo de la Pila y Subrutinas", 10.0, 10.0, "14/09/2026", "Convenio de llamada System V perfecto."),
                            new ActividadNota("Proyecto Assembler: Decodificador Binario", 15.0, 14.5, "28/09/2026", "Máximo rendimiento en ciclos de CPU."),
                            new ActividadNota("Examen Parcial 2: Interrupciones y BIOS", 15.0, 13.0, "12/10/2026", "Control riguroso de memoria e I/O."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA1004", "Cálculo I", 4, "Sec. 10 (Matutina)", "Dr. Roberto Chang", "#7c3aed",
                    70.0, 68.0, 0.0, 68.0, "En curso",
                    List.of(
                            new ActividadNota("Hoja de Trabajo 1: Límites y Continuidad", 5.0, 5.0, "22/07/2026", "Demostraciones delta-épsilon correctas."),
                            new ActividadNota("Examen Corto 1: Regla de la Cadena", 5.0, 5.0, "05/08/2026", "Desarrollo analítico riguroso."),
                            new ActividadNota("Examen Parcial 1: Derivadas y Reglas", 25.0, 24.5, "26/08/2026", "Puntaje casi perfecto en optimización."),
                            new ActividadNota("Hoja de Trabajo 2: Tasas Relacionadas", 10.0, 9.5, "16/09/2026", "Solución geométrica elegante."),
                            new ActividadNota("Examen Parcial 2: Integrales Definidas", 25.0, 24.0, "14/10/2026", "Métodos de integración impecables."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA2005", "Estadística y Probabilidad", 3, "Sec. 10 (Matutina)", "Inga. Claudia Morales", "#ea580c",
                    70.0, 67.5, 0.0, 67.5, "En curso",
                    List.of(
                            new ActividadNota("Taller 1: Análisis Exploratorio de Datos", 10.0, 10.0, "24/07/2026", "Interpretación estadística sobresaliente."),
                            new ActividadNota("Examen Parcial 1: Probabilidad Clásica y Bayes", 20.0, 19.5, "28/08/2026", "Modelado de eventos independientes exacto."),
                            new ActividadNota("Taller 2: Modelado con R y Python", 10.0, 10.0, "18/09/2026", "Script estructurado y visualización pulcra."),
                            new ActividadNota("Examen Parcial 2: Distribuciones Continuas", 20.0, 19.0, "16/10/2026", "Cálculo de percentiles y z-scores."),
                            new ActividadNota("Proyecto: Inferencia y Muestreo", 10.0, 9.0, "23/10/2026", "Intervalos de confianza robustos."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3, "Sec. 10 (Matutina)", "Dr. Fernando Morales", "#db2777",
                    75.0, 74.0, 0.0, 74.0, "En curso",
                    List.of(
                            new ActividadNota("Ensayo 1: Estado Constitucional y Democracia", 15.0, 15.0, "30/07/2026", "Postura crítica de gran altura académica."),
                            new ActividadNota("Debate 1: Ética Pública y Transparencia", 15.0, 15.0, "27/08/2026", "Liderazgo en debate y solidez conceptual."),
                            new ActividadNota("Examen Parcial 1: Marco Legal e Institucional", 20.0, 20.0, "24/09/2026", "Dominio pleno del ordenamiento jurídico."),
                            new ActividadNota("Fase 1: Diagnóstico de Iniciativa Comunitaria", 25.0, 24.0, "15/10/2026", "Propuesta de alto impacto y factibilidad."),
                            new ActividadNota("Presentación Final del Proyecto", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1001", "Guatemala en el Contexto Global", 3, "Sec. 10 (Matutina)", "Inga. Sofía Castillo", "#0d9488",
                    70.0, 69.0, 0.0, 69.0, "En curso",
                    List.of(
                            new ActividadNota("Lectura Crítica 1: Historia Económica", 10.0, 10.0, "28/07/2026", "Excelente marco de análisis comparativo."),
                            new ActividadNota("Examen Parcial 1: Tratados Comerciales", 20.0, 20.0, "01/09/2026", "Respuestas analíticas profundas."),
                            new ActividadNota("Estudio de Caso: Competitividad e Innovación", 15.0, 15.0, "22/09/2026", "Propuesta innovadora y viable."),
                            new ActividadNota("Examen Parcial 2: Retos Geopolíticos", 25.0, 24.0, "20/10/2026", "Visión global fundamentada en datos."),
                            new ActividadNota("Ensayo Final Integrador", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            return list;
        }

        if (carnet == 26710) {
            list.add(new NotaCurso("CC2008", "Programación Orientada a Objetos", 4, "Sec. 10 (Matutina)", "Ing. Carlos Mendoza", "#0284c7",
                    75.0, 51.0, 0.0, 51.0, "En curso",
                    List.of(
                            new ActividadNota("Laboratorio 1: Modelado de Clases y Objetos", 5.0, 3.5, "18/07/2026", "Faltó encapsular atributos privados."),
                            new ActividadNota("Corto 1: Encapsulamiento y Constructores", 5.0, 3.5, "25/07/2026", "Concepto regular de sobrecarga."),
                            new ActividadNota("Laboratorio 2: Polimorfismo e Interfaces", 10.0, 7.0, "08/08/2026", "Interfaz implementada parcialmente."),
                            new ActividadNota("Examen Parcial 1: Fundamentos POO", 20.0, 13.0, "22/08/2026", "Confusión en relación de agregación."),
                            new ActividadNota("Laboratorio 3: Manejo de Excepciones y Colecciones", 10.0, 7.0, "05/09/2026", "Faltó manejo de NullPointerException."),
                            new ActividadNota("Proyecto Fase 1: Arquitectura de Software", 15.0, 11.0, "26/09/2026", "Estructura funcional básica."),
                            new ActividadNota("Examen Parcial 2: Patrones y Estructuras", 10.0, 6.0, "10/10/2026", "Patrón Singleton incompleto."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4, "Sec. 10 (Matutina)", "Ing. Alejandro Morales", "#059669",
                    75.0, 45.0, 0.0, 45.0, "En curso",
                    List.of(
                            new ActividadNota("Práctica 1: Arquitectura de Registros x86-64", 5.0, 3.0, "20/07/2026", "Errores en sintaxis NASM."),
                            new ActividadNota("Laboratorio 1: Aritmética y Banderas de Estado", 10.0, 6.0, "03/08/2026", "Bandera de acarreo no considerada."),
                            new ActividadNota("Examen Parcial 1: Modos de Direccionamiento", 20.0, 12.0, "24/08/2026", "Desplazamientos de memoria incorrectos."),
                            new ActividadNota("Laboratorio 2: Manejo de la Pila y Subrutinas", 10.0, 6.5, "14/09/2026", "Desbalance en registros base."),
                            new ActividadNota("Proyecto Assembler: Decodificador Binario", 15.0, 9.5, "28/09/2026", "Lógica funcional con ciclos redundantes."),
                            new ActividadNota("Examen Parcial 2: Interrupciones y BIOS", 15.0, 8.0, "12/10/2026", "Confusión entre interrupciones 21h y 10h."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA1004", "Cálculo I", 4, "Sec. 10 (Matutina)", "Dr. Roberto Chang", "#7c3aed",
                    70.0, 43.0, 0.0, 43.0, "En curso",
                    List.of(
                            new ActividadNota("Hoja de Trabajo 1: Límites y Continuidad", 5.0, 3.0, "22/07/2026", "Errores en indeterminaciones 0/0."),
                            new ActividadNota("Examen Corto 1: Regla de la Cadena", 5.0, 3.0, "05/08/2026", "Omisión de derivadas internas."),
                            new ActividadNota("Examen Parcial 1: Derivadas y Reglas", 25.0, 15.0, "26/08/2026", "Dificultad en diferenciación logarítmica."),
                            new ActividadNota("Hoja de Trabajo 2: Tasas Relacionadas", 10.0, 6.0, "16/09/2026", "Planteamiento con signos invertidos."),
                            new ActividadNota("Examen Parcial 2: Integrales Definidas", 25.0, 16.0, "14/10/2026", "Integración por partes con errores de signo."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA2005", "Estadística y Probabilidad", 3, "Sec. 10 (Matutina)", "Inga. Claudia Morales", "#ea580c",
                    70.0, 53.5, 0.0, 53.5, "En curso",
                    List.of(
                            new ActividadNota("Taller 1: Análisis Exploratorio de Datos", 10.0, 7.5, "24/07/2026", "Gráficos legibles sin leyendas."),
                            new ActividadNota("Examen Parcial 1: Probabilidad Clásica y Bayes", 20.0, 15.0, "28/08/2026", "Buena aplicación de teorema de Bayes."),
                            new ActividadNota("Taller 2: Modelado con R y Python", 10.0, 7.0, "18/09/2026", "Comentarios de código escasos."),
                            new ActividadNota("Examen Parcial 2: Distribuciones Continuas", 20.0, 16.0, "16/10/2026", "Cálculo de áreas normalizado regular."),
                            new ActividadNota("Proyecto: Inferencia y Muestreo", 10.0, 8.0, "23/10/2026", "Muestra representativa adecuada."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3, "Sec. 10 (Matutina)", "Dr. Fernando Morales", "#db2777",
                    75.0, 58.0, 0.0, 58.0, "En curso",
                    List.of(
                            new ActividadNota("Ensayo 1: Estado Constitucional y Democracia", 15.0, 12.0, "30/07/2026", "Argumento descriptivo pero claro."),
                            new ActividadNota("Debate 1: Ética Pública y Transparencia", 15.0, 11.0, "27/08/2026", "Intervenciones oportunas."),
                            new ActividadNota("Examen Parcial 1: Marco Legal e Institucional", 20.0, 16.0, "24/09/2026", "Respuestas conceptuales acertadas."),
                            new ActividadNota("Fase 1: Diagnóstico de Iniciativa Comunitaria", 25.0, 19.0, "15/10/2026", "Problema delimitado correctamente."),
                            new ActividadNota("Presentación Final del Proyecto", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1001", "Guatemala en el Contexto Global", 3, "Sec. 10 (Matutina)", "Inga. Sofía Castillo", "#0d9488",
                    70.0, 48.0, 0.0, 48.0, "En curso",
                    List.of(
                            new ActividadNota("Lectura Crítica 1: Historia Económica", 10.0, 7.0, "28/07/2026", "Resumen adecuado del contexto."),
                            new ActividadNota("Examen Parcial 1: Tratados Comerciales", 20.0, 13.0, "01/09/2026", "Respuestas breves sin suficiente análisis."),
                            new ActividadNota("Estudio de Caso: Competitividad e Innovación", 15.0, 11.0, "22/09/2026", "Datos económicos coherentes."),
                            new ActividadNota("Examen Parcial 2: Retos Geopolíticos", 25.0, 17.0, "20/10/2026", "Buen marco geográfico."),
                            new ActividadNota("Ensayo Final Integrador", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            return list;
        }

        list.add(new NotaCurso("CC2008", "Programación Orientada a Objetos", 4, "Sec. 10 (Matutina)", "Ing. Carlos Mendoza", "#0284c7",
                75.0, 65.0, 0.0, 65.0, "En curso",
                List.of(
                        new ActividadNota("Laboratorio 1: Modelado de Clases y Objetos", 5.0, 5.0, "18/07/2026", "Excelente modelado con paquetes."),
                        new ActividadNota("Corto 1: Encapsulamiento y Constructores", 5.0, 4.0, "25/07/2026", "Correcta visibilidad de métodos."),
                        new ActividadNota("Laboratorio 2: Polimorfismo e Interfaces", 10.0, 9.0, "08/08/2026", "Sobrecarga y ligadura dinámica clara."),
                        new ActividadNota("Examen Parcial 1: Fundamentos POO", 20.0, 17.0, "22/08/2026", "Buen análisis conceptual en diagramas."),
                        new ActividadNota("Laboratorio 3: Manejo de Excepciones y Colecciones", 10.0, 8.5, "05/09/2026", "Uso correcto de ArrayList y HashMap."),
                        new ActividadNota("Proyecto Fase 1: Arquitectura de Software", 15.0, 13.5, "26/09/2026", "Arquitectura en capas bien estructurada."),
                        new ActividadNota("Examen Parcial 2: Patrones y Estructuras", 10.0, 8.0, "10/10/2026", "Patrones Factory y DAO aplicados."),
                        new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4, "Sec. 10 (Matutina)", "Ing. Alejandro Morales", "#059669",
                75.0, 48.5, 0.0, 48.5, "En curso",
                List.of(
                        new ActividadNota("Práctica 1: Arquitectura de Registros x86-64", 5.0, 4.0, "20/07/2026", "Uso correcto de rax, rbx y rcx."),
                        new ActividadNota("Laboratorio 1: Aritmética y Banderas de Estado", 10.0, 7.0, "03/08/2026", "Manejo adecuado de carry flag."),
                        new ActividadNota("Examen Parcial 1: Modos de Direccionamiento", 20.0, 13.0, "24/08/2026", "Buen uso de direccionamiento indirecto."),
                        new ActividadNota("Laboratorio 2: Manejo de la Pila y Subrutinas", 10.0, 7.0, "14/09/2026", "Retorno de función sin desborde."),
                        new ActividadNota("Proyecto Assembler: Decodificador Binario", 15.0, 10.0, "28/09/2026", "Decodificación funcional en consola."),
                        new ActividadNota("Examen Parcial 2: Interrupciones y BIOS", 15.0, 7.5, "12/10/2026", "Dificultad en manejo de punteros de video."),
                        new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("MA1004", "Cálculo I", 4, "Sec. 10 (Matutina)", "Dr. Roberto Chang", "#7c3aed",
                70.0, 38.0, 0.0, 38.0, "En curso",
                List.of(
                        new ActividadNota("Hoja de Trabajo 1: Límites y Continuidad", 5.0, 3.0, "22/07/2026", "Límites laterales resueltos."),
                        new ActividadNota("Examen Corto 1: Regla de la Cadena", 5.0, 2.5, "05/08/2026", "Faltó simplificar expresiones."),
                        new ActividadNota("Examen Parcial 1: Derivadas y Reglas", 25.0, 13.0, "26/08/2026", "Derivadas trigonométricas con errores."),
                        new ActividadNota("Hoja de Trabajo 2: Tasas Relacionadas", 10.0, 5.0, "16/09/2026", "Ecuación diferencial mal planteada."),
                        new ActividadNota("Examen Parcial 2: Integrales Definidas", 25.0, 14.5, "14/10/2026", "Sustitución simple realizada correctamente."),
                        new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("MA2005", "Estadística y Probabilidad", 3, "Sec. 10 (Matutina)", "Inga. Claudia Morales", "#ea580c",
                70.0, 57.0, 0.0, 57.0, "En curso",
                List.of(
                        new ActividadNota("Taller 1: Análisis Exploratorio de Datos", 10.0, 8.5, "24/07/2026", "Histogramas bien calculados."),
                        new ActividadNota("Examen Parcial 1: Probabilidad Clásica y Bayes", 20.0, 17.0, "28/08/2026", "Dominio de probabilidad condicional."),
                        new ActividadNota("Taller 2: Modelado con R y Python", 10.0, 8.0, "18/09/2026", "Dataframe manipulado con destreza."),
                        new ActividadNota("Examen Parcial 2: Distribuciones Continuas", 20.0, 16.5, "16/10/2026", "Correcto uso de tablas de distribución Z."),
                        new ActividadNota("Proyecto: Inferencia y Muestreo", 10.0, 7.0, "23/10/2026", "Pruebas de hipótesis con p-value claro."),
                        new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3, "Sec. 10 (Matutina)", "Dr. Fernando Morales", "#db2777",
                75.0, 68.0, 0.0, 68.0, "En curso",
                List.of(
                        new ActividadNota("Ensayo 1: Estado Constitucional y Democracia", 15.0, 14.0, "30/07/2026", "Gran profundidad de análisis."),
                        new ActividadNota("Debate 1: Ética Pública y Transparencia", 15.0, 13.5, "27/08/2026", "Argumentos bien formulados."),
                        new ActividadNota("Examen Parcial 1: Marco Legal e Institucional", 20.0, 18.0, "24/09/2026", "Respuestas teóricas precisas."),
                        new ActividadNota("Fase 1: Diagnóstico de Iniciativa Comunitaria", 25.0, 22.5, "15/10/2026", "Propuesta estructurada y con fuentes."),
                        new ActividadNota("Presentación Final del Proyecto", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("SH1001", "Guatemala en el Contexto Global", 3, "Sec. 10 (Matutina)", "Inga. Sofía Castillo", "#0d9488",
                70.0, 51.5, 0.0, 51.5, "En curso",
                List.of(
                        new ActividadNota("Lectura Crítica 1: Historia Económica", 10.0, 7.5, "28/07/2026", "Buen resumen de hechos clave."),
                        new ActividadNota("Examen Parcial 1: Tratados Comerciales", 20.0, 14.0, "01/09/2026", "Respuestas analíticas correctas."),
                        new ActividadNota("Estudio de Caso: Competitividad e Innovación", 15.0, 11.5, "22/09/2026", "Datos económicos pertinentes."),
                        new ActividadNota("Examen Parcial 2: Retos Geopolíticos", 25.0, 18.5, "20/10/2026", "Visión amplia de comercio exterior."),
                        new ActividadNota("Ensayo Final Integrador", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));

        return list;
    }

    @GetMapping({"/notas", "/{carnet}/notas"})
    public ResponseEntity<Map<String, Object>> obtenerNotasEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.notFound().build();
        }

        List<Integer> idsAsignados = asignacionesPorCarnet.getOrDefault(carnetFinal, Collections.emptyList());
        List<NotaCurso> notas = new ArrayList<>();

        List<String> codigosActivos = new ArrayList<>();
        Map<String, SeccionCatalogo> mapaSeccion = new LinkedHashMap<>();
        for (Integer idSec : idsAsignados) {
            SeccionCatalogo sec = buscarSeccionCatalogo(idSec);
            if (sec != null) {
                codigosActivos.add(sec.codigoCurso);
                mapaSeccion.put(sec.codigoCurso, sec);
            }
        }

        List<NotaCurso> catalogoNotas = generarMockNotas(carnetFinal);
        for (NotaCurso nc : catalogoNotas) {
            if (codigosActivos.isEmpty() || codigosActivos.contains(nc.codigoCurso)) {
                if (mapaSeccion.containsKey(nc.codigoCurso)) {
                    SeccionCatalogo s = mapaSeccion.get(nc.codigoCurso);
                    nc.seccion = "Sec. " + s.codigoSeccion + " (" + s.jornada + ")";
                    nc.profesor = s.profesor;
                }
                notas.add(nc);
            }
        }

        double sumaPonderada = 0;
        int totalCreditos = 0;
        for (NotaCurso n : notas) {
            sumaPonderada += (n.zonaObtenida / n.zonaTotal * 100.0) * n.creditos;
            totalCreditos += n.creditos;
        }
        double promedioZona = totalCreditos > 0 ? Math.round((sumaPonderada / totalCreditos) * 10.0) / 10.0 : 0.0;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("estudiante", est.getNombre() + " " + est.getApellido());
        resp.put("periodo", "Segundo Semestre 2026");
        resp.put("promedioZona", promedioZona);
        resp.put("totalCreditos", totalCreditos);
        resp.put("totalCursos", notas.size());
        resp.put("cursos", notas);

        return ResponseEntity.ok(resp);
    }

    public record CursoHistorial(String codigo, String nombre, int creditos, double notaFinal, String estado, String periodo) {}
    public record SemestreHistorial(String periodo, double promedioSemestre, int creditosSemestre, List<CursoHistorial> cursos) {}

    @GetMapping({"/historial", "/{carnet}/historial"})
    public ResponseEntity<Map<String, Object>> obtenerHistorialEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.notFound().build();
        }

        List<SemestreHistorial> semestres = new ArrayList<>();

        if (carnetFinal == 26547) {
            List<CursoHistorial> s1Cursos = List.of(
                    new CursoHistorial("CC1001", "Introducción a la Programación", 4, 88.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("MA1001", "Precálculo y Álgebra Lineal", 4, 84.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("FS1001", "Física I (Mecánica Clásica)", 4, 82.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("CO1001", "Comunicación y Redacción Técnica", 3, 90.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("QU1001", "Química General para Computación", 3, 85.0, "Aprobado", "Primer Semestre 2025")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2025", 85.8, 18, s1Cursos));

            List<CursoHistorial> s2Cursos = List.of(
                    new CursoHistorial("CC1002", "Algoritmos y Estructuras de Datos I", 4, 91.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("MA1002", "Cálculo Diferencial", 4, 86.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("FS1002", "Física II (Electricidad y Magnetismo)", 4, 83.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("CC1003", "Lógica Computacional y Discretas", 3, 89.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("ME1001", "Metodología de la Investigación", 3, 87.0, "Aprobado", "Segundo Semestre 2025")
            );
            semestres.add(new SemestreHistorial("Segundo Semestre 2025", 87.3, 18, s2Cursos));

            List<CursoHistorial> s3Cursos = List.of(
                    new CursoHistorial("CC2001", "Algoritmos y Estructuras de Datos II", 4, 92.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("MA1003", "Cálculo Integral", 4, 85.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CC2002", "Arquitectura y Organización de Computadores I", 4, 88.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CC2004", "Sistemas Operativos (Fundamentos)", 4, 86.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("ET1001", "Ética en la Era de la Información", 3, 93.0, "Aprobado", "Primer Semestre 2026")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2026", 88.7, 19, s3Cursos));
        } else if (carnetFinal == 26000) {
            List<CursoHistorial> s1Cursos = List.of(
                    new CursoHistorial("CC1001", "Introducción a la Programación", 4, 96.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("MA1001", "Precálculo y Álgebra Lineal", 4, 94.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("FS1001", "Física I (Mecánica Clásica)", 4, 92.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CO1001", "Comunicación y Redacción Técnica", 3, 98.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("QU1001", "Química General para Computación", 3, 96.0, "Aprobado", "Primer Semestre 2026")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2026", 95.2, 18, s1Cursos));
        } else {
            List<CursoHistorial> s1Cursos = List.of(
                    new CursoHistorial("CC1001", "Introducción a la Programación", 4, 72.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("MA1001", "Precálculo y Álgebra Lineal", 4, 68.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("FS1001", "Física I (Mecánica Clásica)", 4, 65.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CO1001", "Comunicación y Redacción Técnica", 3, 76.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("QU1001", "Química General para Computación", 3, 74.0, "Aprobado", "Primer Semestre 2026")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2026", 71.0, 18, s1Cursos));
        }

        int creditosTotales = 0;
        int cursosAprobados = 0;
        double sumaNotasPonderadas = 0;
        for (SemestreHistorial s : semestres) {
            for (CursoHistorial c : s.cursos()) {
                if ("Aprobado".equalsIgnoreCase(c.estado())) {
                    creditosTotales += c.creditos();
                    cursosAprobados++;
                    sumaNotasPonderadas += (c.notaFinal() * c.creditos());
                }
            }
        }
        double promedioAcumulado = creditosTotales > 0 ? Math.round((sumaNotasPonderadas / creditosTotales) * 10.0) / 10.0 : 0.0;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("estudiante", est.getNombre() + " " + est.getApellido());
        resp.put("carrera", "Ingeniería en Ciencias de la Computación");
        resp.put("facultad", "Facultad de Ingeniería");
        resp.put("promedioAcumulado", promedioAcumulado);
        resp.put("creditosAprobados", creditosTotales);
        resp.put("cursosAprobados", cursosAprobados);
        resp.put("eficienciaAcademica", "100%");
        resp.put("semestres", semestres);

        return ResponseEntity.ok(resp);
    }

    @GetMapping({"/perfil", "/{carnet}/perfil"})
    public ResponseEntity<Map<String, Object>> obtenerPerfilEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("nombre", est.getNombre());
        resp.put("apellido", est.getApellido());
        resp.put("nombreCompleto", est.getNombre() + " " + est.getApellido());
        resp.put("fechaNacimiento", est.getFechaNacimiento() != null ? est.getFechaNacimiento() : "2005-04-12");
        resp.put("correoInstitucional", est.getCorreo() != null ? est.getCorreo() : "estudiante" + est.getCarnet() + "@uvg.edu.gt");
        resp.put("correoPersonal", "contacto." + est.getCarnet() + "@gmail.com");
        resp.put("telefono", est.getTelefono() > 0 ? String.valueOf(est.getTelefono()) : "22458900");
        resp.put("direccion", est.getDireccion() != null && !est.getDireccion().isBlank() ? est.getDireccion() : "Ciudad de Guatemala, Guatemala");
        resp.put("carrera", "Ingeniería en Ciencias de la Computación");
        resp.put("facultad", "Facultad de Ingeniería");
        resp.put("fechaIngreso", est.getFechaIngreso() > 0 ? String.valueOf(est.getFechaIngreso()) : "2025");
        resp.put("estado", est.getEstado() != null ? est.getEstado() : "Activo Regular");
        resp.put("promedioGeneral", est.getPromedioGeneral() > 0 ? est.getPromedioGeneral() : 87.3);
        resp.put("periodoActual", "Segundo Semestre 2026");
        resp.put("campus", "Campus Central");
        resp.put("jornada", "Mixta (Matutina / Nocturna)");

        return ResponseEntity.ok(resp);
    }

    @PutMapping({"/perfil", "/{carnet}/perfil"})
    public ResponseEntity<Map<String, Object>> actualizarPerfilEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> datos,
            HttpSession sesion) {
        int carnetFinal = resolverCarnet(carnet, sesion);
        Estudiante est = buscarPorCarnetInterno(carnetFinal);
        if (est == null) {
            return ResponseEntity.notFound().build();
        }

        if (datos.containsKey("telefono")) {
            try {
                String telStr = String.valueOf(datos.get("telefono")).replaceAll("[^0-9]", "");
                if (!telStr.isBlank()) {
                    est.setTelefono(Integer.parseInt(telStr));
                }
            } catch (Exception ignored) {}
        }
        if (datos.containsKey("direccion")) {
            est.setDireccion(String.valueOf(datos.get("direccion")).trim());
        }
        guardar();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("ok", true);
        resp.put("mensaje", "Perfil actualizado correctamente.");
        return ResponseEntity.ok(resp);
    }
}

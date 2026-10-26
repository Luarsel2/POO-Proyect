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
            Estudiante fallback = new Estudiante(26547, "Luis", "Garcia", "2000-01-01", "prueba26547@uvg.edu.gt",
                    0, "Guatemala", 26547, 2026, "Activo", 85.0, null, null, 26547);
            listaEstudiantes.add(fallback);
            return fallback;
        }
        if (carnet == 26710) {
            Estudiante fallback = new Estudiante(26710, "Gabriel", "Yanes", "2000-01-01", "prueba26710@uvg.edu.gt",
                    0, "Guatemala", 26710, 2026, "Activo", 85.0, null, null, 26710);
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
}

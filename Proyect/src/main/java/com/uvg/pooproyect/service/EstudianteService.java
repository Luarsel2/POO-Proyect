package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.EstudianteRequest;
import com.uvg.pooproyect.dto.RespuestaAcademica.*;
import com.uvg.pooproyect.model.*;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EstudianteService {
    private final EstudianteRepository estudianteRepository;

    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    public Map<String, String> registrarEstudiante(EstudianteRequest req) {
        Estudiante nuevoEstudiante = new Estudiante(
                req.id(), req.nombre(), req.apellido(), req.fechaNacimiento(), req.correo(),
                req.telefono(), req.direccion(), req.carnet(), req.fechaIngreso(), req.estado(),
                req.promedioGeneral(), null, null, req.idHistorial()
        );
        estudianteRepository.guardar(nuevoEstudiante);
        return Map.of("mensaje", "Estudiante " + req.nombre() + " " + req.apellido()
                + " registrado exitosamente. Carnet: " + req.carnet());
    }

    public Map<String, String> inscribirCurso(int carnet) {
        Estudiante estudiante = estudianteRepository.buscarPorCarnet(carnet);
        if (estudiante != null) {
            estudiante.inscribirCurso();
            return Map.of("mensaje", "Curso inscrito exitosamente para el carnet: " + carnet);
        }
        return Map.of("mensaje", "Error: Estudiante con carnet " + carnet + " no encontrado.");
    }

    public Estudiante buscarPorCarnet(int carnet) {
        return estudianteRepository.buscarPorCarnet(carnet);
    }

    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.obtenerTodos();
    }

    public Map<String, Object> obtenerPerfil(int carnetFinal) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return null;
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

        return resp;
    }

    public Map<String, Object> actualizarPerfil(int carnetFinal, Map<String, Object> datos) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return null;
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
        estudianteRepository.sincronizarEstudiantes();

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("ok", true);
        resp.put("mensaje", "Perfil actualizado correctamente.");
        return resp;
    }
}

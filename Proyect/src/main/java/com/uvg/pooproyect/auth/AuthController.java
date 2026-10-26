package com.uvg.pooproyect.auth;

import com.uvg.pooproyect.controller.EstudianteController;
import com.uvg.pooproyect.model.Estudiante;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// Estudiante: usuario = carnet, contraseña = la que asigna el administrador
// en la pagina Cuentas (al crear la cuenta). Cuentas ya existentes al
// primer arranque: contraseña inicial = correo del estudiante.
// Administrador: usuario admin, contraseña admin123 (application.properties)

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_ESTUDIANTE = "ESTUDIANTE";

    public record LoginRequest(String usuario, String clave) {}

    private final EstudianteController estudianteController;
    private final CuentaController cuentaController;
    private final String adminUsuario;
    private final String adminClave;

    public AuthController(EstudianteController estudianteController,
                          CuentaController cuentaController,
                          @Value("${portal.admin.usuario:admin}") String adminUsuario,
                          @Value("${portal.admin.clave:admin123}") String adminClave) {
        this.estudianteController = estudianteController;
        this.cuentaController = cuentaController;
        this.adminUsuario = adminUsuario;
        this.adminClave = adminClave;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        String usuario = req.usuario() == null ? "" : req.usuario().trim();
        String clave = req.clave() == null ? "" : req.clave().trim();

        // 1) ¿Es el administrador?
        if (usuario.equals(adminUsuario) && clave.equals(adminClave)) {
            HttpSession nueva = sesionNueva(request);
            nueva.setAttribute("rol", ROL_ADMIN);
            nueva.setAttribute("nombre", "Administrador");
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Bienvenido, administrador.",
                    "rol", ROL_ADMIN,
                    "redirect", "index.html"));
        }

        // 2) ¿Es un estudiante con cuenta? usuario = carnet
        try {
            int carnet = Integer.parseInt(usuario);
            Estudiante e = estudianteController.buscarPorCarnet(carnet);
            if (e != null && cuentaController.verificar(carnet, clave)) {
                HttpSession nueva = sesionNueva(request);
                nueva.setAttribute("rol", ROL_ESTUDIANTE);
                nueva.setAttribute("carnet", carnet);
                nueva.setAttribute("nombre", e.getNombre() + " " + e.getApellido());
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Bienvenido, " + e.getNombre() + ".",
                        "rol", ROL_ESTUDIANTE,
                        "redirect", "dashboard-estudiante.html"));
            }
        } catch (NumberFormatException ignorado) {
            // el usuario no era un número: no puede ser un carnet
        }

        // Mismo mensaje para "usuario no existe" y "clave incorrecta" a propósito.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Usuario o contraseña incorrectos."));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession sesion) {
        sesion.invalidate();
        return Map.of("mensaje", "Sesión cerrada.");
    }

    // La usan las páginas para saber quién está conectado.
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(HttpSession sesion) {
        Object rol = sesion.getAttribute("rol");
        if (rol == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Sin sesión."));
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("rol", rol.toString());
        resp.put("nombre", String.valueOf(sesion.getAttribute("nombre")));
        if (sesion.getAttribute("carnet") != null) {
            resp.put("carnet", sesion.getAttribute("carnet"));
        }
        return ResponseEntity.ok(resp);
    }

    // Descarta cualquier sesión anterior y crea una nueva en cada login.
    private HttpSession sesionNueva(HttpServletRequest request) {
        HttpSession vieja = request.getSession(false);
        if (vieja != null) vieja.invalidate();
        return request.getSession(true);
    }
}

package com.uvg.pooproyect.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// usuario de estudiante es: 26547 y contraseña es prueba26547@uvg.edu.gt
// usuario de admin es: admin y contraseña admin123

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String ROL_ADMIN = Roles.ADMIN;
    public static final String ROL_ESTUDIANTE = Roles.ESTUDIANTE;
    public record LoginRequest(String usuario, String clave) {}
    private final com.uvg.pooproyect.service.AuthService service;
    public AuthController(com.uvg.pooproyect.service.AuthService service) { this.service = service; }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        var acceso = service.autenticar(req.usuario(), req.clave());
        if (acceso.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("mensaje", "Usuario o contraseña incorrectos."));
        var usuario = acceso.get();
        HttpSession sesion = sesionNueva(request);
        sesion.setAttribute("rol", usuario.rol());
        sesion.setAttribute("nombre", usuario.nombre());
        if (usuario.carnet() != null) sesion.setAttribute("carnet", usuario.carnet());
        return ResponseEntity.ok(Map.of("mensaje", usuario.mensaje(), "rol", usuario.rol(), "redirect", usuario.redirect()));
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
        } else if (ROL_ADMIN.equals(rol)) {
            resp.put("carnet", 26547);
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

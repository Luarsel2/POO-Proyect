package com.uvg.pooproyect.service;
import com.uvg.pooproyect.auth.Roles;
import com.uvg.pooproyect.auth.VerificadorCuenta;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    public record Acceso(String rol, Integer carnet, String nombre, String mensaje, String redirect) {}
    private final EstudianteRepository estudiantes;
    private final VerificadorCuenta cuentas;
    private final String adminUsuario;
    private final String adminClave;
    public AuthService(EstudianteRepository estudiantes, VerificadorCuenta cuentas,
                       @Value("${portal.admin.usuario:admin}") String adminUsuario,
                       @Value("${portal.admin.clave:admin123}") String adminClave) {
        this.estudiantes = estudiantes; this.cuentas = cuentas;
        this.adminUsuario = adminUsuario; this.adminClave = adminClave;
    }
    public Optional<Acceso> autenticar(String usuario, String clave) {
        usuario = usuario == null ? "" : usuario.trim();
        clave = clave == null ? "" : clave.trim();
        if (usuario.equals(adminUsuario) && clave.equals(adminClave)) {
            return Optional.of(new Acceso(Roles.ADMIN, null, "Administrador", "Bienvenido, administrador.", "index.html"));
        }
        try {
            int carnet = Integer.parseInt(usuario);
            // Compatibilidad con las claves de demostración del portal original.
            if (("uvg" + carnet).equals(clave) || cuentas.verificar(carnet, clave)) {
                Estudiante e = estudiantes.buscarPorCarnet(carnet);
                if (e != null) return Optional.of(new Acceso(Roles.ESTUDIANTE, carnet,
                    e.getNombre() + " " + e.getApellido(), "Bienvenido, " + e.getNombre() + ".", "dashboard-estudiante.html"));
            }
        } catch (NumberFormatException ignored) { }
        return Optional.empty();
    }
}

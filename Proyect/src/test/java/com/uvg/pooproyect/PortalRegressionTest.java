package com.uvg.pooproyect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PortalRegressionTest {
    static final Path DATOS;
    static {
        try {
            DATOS = Files.createTempDirectory("portal-regression-");
            System.setProperty("portal.data-dir", DATOS.toString());
            Files.writeString(DATOS.resolve("estudiantes.csv"),
                "26547,Luis,Garcia,2004-09-18,prueba26547@uvg.edu.gt,52147890,Guatemala,26547,2025,Activo,82.5,26547\n");
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    @Autowired MockMvc mvc;

    private MockHttpSession admin() throws Exception {
        return (MockHttpSession) mvc.perform(post("/api/auth/login").contentType("application/json")
            .content("{\"usuario\":\"admin\",\"clave\":\"admin123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.rol").value("ADMIN"))
            .andReturn().getRequest().getSession(false);
    }

    @Test void accesoYConsultasDelEstudiante() throws Exception {
        mvc.perform(get("/api/cursos")).andExpect(status().isUnauthorized());
        MockHttpSession sesion = (MockHttpSession) mvc.perform(post("/api/auth/login")
            .contentType("application/json").content("{\"usuario\":\"26547\",\"clave\":\"uvg26547\"}"))
            .andExpect(status().isOk()).andReturn().getRequest().getSession(false);
        for (String ruta : new String[]{"dashboard", "cursos-disponibles", "horario", "notas", "historial", "perfil"}) {
            mvc.perform(get("/api/estudiantes/26547/" + ruta).session(sesion))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json"));
        }
        mvc.perform(get("/api/cuentas").session(sesion)).andExpect(status().isForbidden());
        mvc.perform(post("/api/estudiantes/26547/retirar-todos").session(sesion)).andExpect(status().isOk());
        mvc.perform(post("/api/estudiantes/26547/asignar-curso").session(sesion)
            .contentType("application/json").content("{\"idSeccion\":101}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        mvc.perform(post("/api/estudiantes/26547/asignar-curso").session(sesion)
            .contentType("application/json").content("{\"idSeccion\":101}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.ok").value(false));
    }

    @Test void flujoAdministrativoConRelaciones() throws Exception {
        MockHttpSession s = admin();
        enviar(s, "/api/cursos", "{\"nombre\":\"POO Test\",\"creditos\":4}");
        enviar(s, "/api/carreras", "{\"nombre\":\"Computacion Test\",\"totalCreditos\":\"200\",\"duracionPromedio\":5}");
        enviar(s, "/api/carreras/Computacion Test/planes", "{\"year\":2026,\"estado\":\"ACTIVO\"}");
        enviar(s, "/api/carreras/Computacion Test/planes/2026/cursos/POO Test", "{}");
        enviar(s, "/api/facultades", "{\"nombre\":\"Ingenieria Test\"}");
        enviar(s, "/api/facultades/Ingenieria Test/carreras/Computacion Test", "{}");
        enviar(s, "/api/profesores", "{\"id\":900,\"nombre\":\"Ana\",\"apellido\":\"Test\",\"codigoDeEmpleado\":900,\"especialidad\":\"POO\"}");
        enviar(s, "/api/administracion", "{\"id\":901,\"nombre\":\"Admin\",\"apellido\":\"Test\",\"codigoDeEmpleado\":901}");
        enviar(s, "/api/secciones", "{\"idSeccion\":900,\"codigo\":90,\"cupoMaximo\":2,\"codigoProfesor\":900}");
        enviar(s, "/api/secciones/900/horarios", "{\"idHorario\":1,\"diaSemana\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"09:00\"}");
        enviar(s, "/api/periodos", "{\"idPeriodo\":900,\"nombre\":\"Periodo Test\",\"estado\":\"ACTIVO\"}");
        enviar(s, "/api/inscripciones", "{\"id\":900,\"carnetEstudiante\":26547,\"idPeriodo\":900,\"estado\":\"ACTIVA\"}");
        enviar(s, "/api/inscripciones/900/detalles", "{\"idDetalle\":900,\"idSeccion\":900,\"estado\":\"ACTIVO\"}");
        mvc.perform(get("/api/carreras").session(s)).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].planesDeEstudios[0].cursos[0].nombre").value("POO Test"));
        mvc.perform(get("/api/secciones").session(s)).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].profesor.nombre").value("Ana"));
        mvc.perform(get("/api/inscripciones").session(s)).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].detalles[0].seccion.idSeccion").value(900));
        for (String ruta : new String[]{"cursos", "facultades", "profesores", "administracion", "periodos", "cuentas", "estudiantes"}) {
            mvc.perform(get("/api/" + ruta).session(s)).andExpect(status().isOk());
        }
    }

    private void enviar(MockHttpSession s, String ruta, String cuerpo) throws Exception {
        mvc.perform(post(ruta).session(s).contentType("application/json").content(cuerpo))
            .andExpect(status().isOk()).andExpect(jsonPath("$.mensaje").exists());
    }
}

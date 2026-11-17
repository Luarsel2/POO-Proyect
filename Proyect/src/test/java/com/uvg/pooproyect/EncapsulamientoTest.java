package com.uvg.pooproyect;
import com.uvg.pooproyect.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EncapsulamientoTest {
    @Test void coleccionesNoPermitenModificarElEstadoDesdeFuera() {
        Facultad facultad = new Facultad("Ingenieria");
        facultad.agregarCarrera(new Carrera("Computacion", "200", 5));
        assertThrows(UnsupportedOperationException.class, () -> facultad.getCarreras().clear());
        assertEquals(1, facultad.getCarreras().size());
        var seccion = new Seccion(1, 10, 20, 0, "ACTIVA", null, null);
        var horarios = new ArrayList<>(List.of(new Horario(1,"Lunes","08:00","09:00")));
        seccion.setHorario(horarios);
        horarios.clear();
        assertEquals(1, seccion.getHorario().size());
        assertThrows(UnsupportedOperationException.class, () -> seccion.getHorario().clear());
    }
    @Test void promedioProtegeSuRangoTantoEnConstructorComoEnSetter() {
        var e = new Estudiante(1,"Ana","Test","2005-01-01","a@uvg.edu.gt",1,"GT",42,2026,"Activo",85,null,null,42);
        assertThrows(IllegalArgumentException.class, () -> e.setPromedioGeneral(101));
        assertThrows(IllegalArgumentException.class, () -> e.setPromedioGeneral(Double.NaN));
        assertEquals(85, e.getPromedioGeneral());
        assertThrows(IllegalArgumentException.class, () -> new Estudiante(1,"Ana","Test","","",0,"",42,2026,"Activo",-1,null,null,42));
    }
}

package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.model.*;
import java.util.*;
import org.springframework.stereotype.Repository;

/** Catálogo de demostración conservado del portal original. */
@Repository
public class CatalogoSeccionesDemo implements CatalogoSeccionRepository {
    private final List<SeccionCatalogo> catalogoSecciones = new ArrayList<>();
    public CatalogoSeccionesDemo() {
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
    public List<SeccionCatalogo> obtenerCatalogoSecciones() {
        return List.copyOf(catalogoSecciones);
    }

    public SeccionCatalogo buscarSeccionCatalogo(int idSeccion) {
        for (SeccionCatalogo s : catalogoSecciones) {
            if (s.idSeccion == idSeccion) {
                return s;
            }
        }
        return null;
    }
}

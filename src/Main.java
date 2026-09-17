import controller.EstudianteController;
import controller.InscripcionController;
import model.Estudiante;

public class Main {
    public static void main(String[] args) {
        System.out.println("---Sistema Gestiones Universitario---");

        // 1. Instanciar los controladores
        EstudianteController estudianteController = new EstudianteController();
        InscripcionController inscripcionController = new InscripcionController();

        // 2. Registrar un estudiante por medio del controlador
        String respuestaRegistro = estudianteController.registrarEstudiante(
            1, "Carlos", "López", "2001-08-20", "clopez@mail.com", 
            55551234, "Guatemala", 2026010, 2026, "Activo", 
            88.5, null, null, 501
        );
        System.out.println(respuestaRegistro);

        // 3. Inscribir un curso usandos una de la logica del controlador de estudiantes
        String respuestaInscripcion = estudianteController.inscribirCursoAEstudiante(2026010);
        System.out.println(respuestaInscripcion);

        // 4. Buscar el estudiante registrado en la lista en memoria
        Estudiante estudianteObtenido = estudianteController.buscarPorCarnet(2026010);

        if (estudianteObtenido != null) {
            System.out.println("Historial Académico ID: " + 
                estudianteObtenido.getHistorialAcademico().getIdHistorial());

            // 5. Probar el controlador de inscripciones usando la instancia encontrada
            String resultadoProceso = inscripcionController.procesarInscripcion(estudianteObtenido, null);
            System.out.println(resultadoProceso);
        }
    }
}
# POO-Proyect
Grupo para el proyecto de POO
- Raul Robles - 26919
- Luis Garcia - 26547
- Gabriel Yanes - 26710

## Ejecución y pruebas

Requiere Java 25 o posterior y Maven. Desde la raíz del repositorio:

```sh
mvn -f Proyect/pom.xml test
mvn -f Proyect/pom.xml package
java -jar Proyect/target/pooproyect.jar
```

El portal se abre en http://localhost:8080/. La carpeta `data/` conserva el formato CSV existente. Para ejecutar una copia aislada se puede especificar `java -Dportal.data-dir=/ruta/a/datos -jar Proyect/target/pooproyect.jar`. Las pruebas usan carpetas temporales y no modifican los datos del proyecto.

## Arquitectura y principios SOLID

El flujo del portal es `HTML/JavaScript → controlador REST → servicio → interfaz de repositorio → implementación CSV`. Los modelos representan entidades del dominio. Las vistas permanecen en `Proyect/src/main/resources/static` y consumen la API mediante `fetch`.

| Principio | Implementación y ejemplo |
| --- | --- |
| S: responsabilidad única | `CursoController` adapta HTTP; `CursoService` coordina operaciones; `CsvCursoRepository` guarda y carga cursos. Estudiantes tiene servicios separados para perfil, consultas académicas y asignaciones. |
| O: abierto/cerrado | `AsignacionService` recibe una lista de `ReglaAsignacion`. Se pueden agregar reglas con `@Component` y `@Order` sin modificar el servicio. Las reglas existentes comprueban cupos y una sola sección por curso. |
| L: sustitución de Liskov | Los repositorios deben cumplir el mismo contrato: buscar un carnet desconocido devuelve `null` sin crear estudiantes; las listas de consulta son instantáneas inmodificables. `EstudianteRepositoryContractTest` ejecuta ese contrato contra CSV y una implementación en memoria. |
| I: segregación de interfaces | `EstudianteRepository`, `AsignacionRepository`, `CatalogoSeccionRepository`, `NotasRepository` e `HistorialRepository` atienden necesidades distintas. El filtro solo conoce `ConsultaCuenta`; autenticación usa `VerificadorCuenta`. |
| D: inversión de dependencias | Los servicios reciben interfaces de repositorios por constructor. `CuentaService` depende de `CodificadorClave`, cuya implementación compatible con los archivos existentes es `Sha256CodificadorClave`. Spring conecta las implementaciones. |

Para añadir una regla, se implementa `Optional<String> validar(List<SeccionCatalogo> anteriores, List<SeccionCatalogo> propuestas)`. `Optional.empty()` permite continuar; un mensaje rechaza la selección. Las reglas deben consultar sin modificar las entidades ni escribir archivos. Todas se evalúan antes de guardar y ajustar cupos. La prueba `permiteAgregarUnaReglaSinModificarElServicio` demuestra la extensión.

El polimorfismo se utiliza al ejecutar las distintas reglas y al sustituir implementaciones de repositorios. La herencia `Persona → Empleado → Profesor/Administracion` y `Persona → Estudiante` se conserva. Las entidades protegen sus listas con copias y el promedio del estudiante valida el rango de 0 a 100. Los cupos del catálogo se modifican mediante operaciones del modelo.

## Comprobaciones y alcance

Las pruebas cubren acceso y sesiones, cuentas y contraseñas, operaciones administrativas con relaciones, recarga de CSV, consultas del estudiante, reglas extensibles, rechazos sin pérdida de la selección, sustitución de repositorios, encapsulamiento y límites entre capas. También verifican la recuperación de horarios `HH:mm` y cupos al reiniciar.

Los archivos se escriben primero en un temporal y luego se reemplazan; los errores de lectura y escritura se propagan y la API devuelve un error en lugar de anunciar éxito. Esto no convierte los CSV en una base de datos transaccional: el proyecto sigue pensado para una sola instancia, sin transacciones entre múltiples archivos.

`CatalogoSeccionesDemo` y `DatosAcademicosDemo` conservan los cursos, notas e historiales de demostración del portal. Las notas e historiales no representan registros académicos persistidos. El catálogo estudiantil de demostración y las secciones creadas en el panel administrativo siguen siendo fuentes distintas, como en la versión original. Para unificarlas se debe implementar `CatalogoSeccionRepository` con una fuente común.

La autenticación conserva las claves de demostración `uvg<carnet>` y el formato SHA-256 de las cuentas existentes. Esta refactorización no sustituye ese esquema por autenticación de producción. Los métodos de ejemplo de algunos modelos que imprimen mensajes en consola siguen siendo demostrativos; las asignaciones efectivas se realizan mediante `AsignacionService`.

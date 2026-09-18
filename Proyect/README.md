# POO-Proyect — Portal de Gestión Universitaria (UVG)

Grupo para el proyecto de POO
- Raul Robles - 26919
- Luis Garcia - 26547
- Gabriel Yanes - 26710

## Qué cambió respecto a la versión anterior

Tenían las clases de `model/` y `controller/` listas, más un `Main.java` que las
probaba por consola, y un `view/index.html` suelto que aún no hablaba con nada.
Lo que se agregó es el "puente" real entre ambas partes, usando **Spring Boot**
tal como lo decidieron en `Investigacion.md`:

- Se convirtió el proyecto a la estructura estándar de Maven
  (`src/main/java/...`) porque así es como Spring Boot y `mvn` esperan
  encontrar los archivos. Es la única razón por la que las carpetas ya no son
  exactamente `src/model`, `src/controller`, `src/view` sueltas — ahora viven
  dentro de `src/main/java/com/uvg/pooproyect/`.
- **Los modelos casi no cambiaron.** Se copiaron tal cual, solo se les ajustó
  el `package` y se agregó `@JsonIgnore` en 5 relaciones que apuntaban en
  ambas direcciones (ej. `Estudiante` ↔ `Inscripcion`), porque si no,
  al convertir un objeto a JSON el servidor entra en un bucle infinito.
  Cada caso tiene un comentario explicando por qué está ahí.
- **Los controladores** (`EstudianteController`, `InscripcionController`) se
  volvieron controladores REST (`@RestController`) — la misma lógica de
  antes, pero ahora expuesta como endpoints HTTP. Se agregaron controladores
  nuevos con el mismo patrón para el resto del modelo: Profesor,
  Administración, Curso, Sección, Facultad, Carrera y Período Académico.
- **El frontend** quedó en `src/main/resources/static/`, que es donde Spring
  Boot sirve archivos estáticos automáticamente. Son 6 páginas HTML simples
  (sin frameworks de JS) con un CSS compartido en tono verde y blanco, y un
  `app.js` con las funciones para hablar con el backend.
- **Persistencia de datos: archivos CSV.** Cada controlador guarda su lista
  en un archivo de texto dentro de la carpeta `data/` (se crea sola la
  primera vez que corres el proyecto), y la vuelve a cargar cuando el
  servidor arranca. Toda la lógica de leer/escribir vive en una sola clase,
  `persistencia/CsvUtil.java`.

### Por qué CSV y no una base de datos

Esto responde directamente al punto de "Investigación de la tecnología
disponible para persistencia de datos" del enunciado:

- Es un prototipo de un solo usuario/servidor, sin necesidad de consultas
  concurrentes ni relaciones complejas a nivel de motor de base de datos.
- No requiere instalar ni levantar un servidor aparte (MySQL, Postgres,
  etc.) — cualquier compañero o el profesor puede correr el proyecto solo
  con Java y Maven, sin configurar nada más.
- Cumple el único requisito real que pedía el enunciado (que los datos
  sobrevivan a un reinicio del servidor) sin agregar dependencias nuevas al
  `pom.xml`.
- Con pocas entidades y pocos registros, no se necesita indexado ni
  transacciones: reescribir el archivo completo en cada cambio es
  suficiente, y además es fácil de depurar (se puede abrir el `.csv` en
  Excel o en un editor de texto y ver los datos a simple vista).
- **Limitación aceptada:** al ser un formato CSV simple (sin comillas), los
  textos no deben llevar comas — el sistema las reemplaza por punto y coma
  automáticamente al guardar para no romper el archivo.

Las relaciones entre entidades (por ejemplo, qué profesor tiene una sección,
o qué estudiante y qué período tiene una inscripción) se guardan como
"llaves foráneas" simples — el código del profesor, el nombre de la carrera,
el carnet del estudiante — y se reconstruyen buscando esos IDs en las demás
listas cuando el servidor arranca.

## Cómo correrlo

Necesitas tener **Java 17+** y **Maven** instalados (o usar el "Maven Wrapper"
si lo agregas desde tu IDE — IntelliJ lo puede generar automáticamente al
abrir un proyecto Maven).

1. Abre la carpeta `POO-Proyect` en IntelliJ como proyecto Maven (File → Open,
   selecciona la carpeta que contiene `pom.xml`). IntelliJ debería reconocer
   Maven y descargar las dependencias de Spring Boot solo.
2. Corre la clase `PooProyectApplication` (botón ▶️ verde), o desde una
   terminal en esa carpeta:
   ```
   mvn spring-boot:run
   ```
3. Abre el navegador en **http://localhost:8080**. Ahí está el portal
   completo, con un menú para moverte entre Estudiantes, Personal, Cursos y
   Secciones, Facultades y Carreras, y Períodos e Inscripciones.

## Estructura del proyecto

```
POO-Proyect/
├── pom.xml
├── Investigacion.md          (tu documento original, sin cambios)
└── src/main/
    ├── java/com/uvg/pooproyect/
    │   ├── PooProyectApplication.java   (reemplaza al Main.java de consola)
    │   ├── model/          (tus 17 clases, casi sin cambios)
    │   ├── controller/     (tus 2 controladores + 7 nuevos, ahora REST)
    │   ├── dto/            (clases pequeñas para recibir los formularios)
    │   └── persistencia/   (CsvUtil: lee y escribe los archivos .csv)
    └── resources/
        ├── application.properties
        └── static/         (el portal: HTML, CSS y JS)
```

## Cosas a tener en cuenta para la exposición

- Si más adelante quieren pasar a una base de datos real, el siguiente paso
  natural es agregar `spring-boot-starter-data-jpa` con H2 o MySQL — pero
  eso implicaría cambios más grandes en el modelo (anotar las clases con
  `@Entity`, etc.), así que se dejó fuera a propósito para mantenerlo simple
  y consistente con lo que pedía el enunciado.
- Algunas relaciones del modelo son de "uno a uno" en vez de listas (por
  ejemplo, `Curso` solo guarda un `Estudiante`, no una lista). Se respetó tal
  cual estaba diseñado; no se cambió la lógica de negocio, solo se conectó
  con el frontend y con la persistencia.
- **Sobre el patrón MVC:** el enunciado marca que las clases Modelo y
  Controlador no pueden mostrar resultados directamente al usuario. Algunos
  métodos del modelo (`inscribirCurso()`, `abrirInscripciones()`, etc.)
  todavía tienen `System.out.println` — eso queda solo en la consola del
  servidor (nadie lo ve en el navegador). Lo que el usuario realmente ve en
  el portal es el mensaje JSON que arma el Controlador y que la Vista
  (HTML/JS) muestra en pantalla, así que la separación real se respeta. Si
  quieren dejarlo perfectamente limpio para la entrega, pueden quitar esos
  `System.out.println` del modelo sin que nada se rompa.

package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.dto.RespuestaAcademica.*;
import org.springframework.stereotype.Repository;
import java.util.*;

/** Datos de demostración del portal original; sustituible por persistencia real. */
@Repository
public class DatosAcademicosDemo implements NotasRepository, HistorialRepository {
    public List<NotaCurso> obtenerNotas(int carnet) {
        List<NotaCurso> list = new ArrayList<>();

        if (carnet == 26000) {
            list.add(new NotaCurso("CC2008", "Programación Orientada a Objetos", 4, "Sec. 10 (Matutina)", "Ing. Carlos Mendoza", "#0284c7",
                    75.0, 73.0, 0.0, 73.0, "En curso",
                    List.of(
                            new ActividadNota("Laboratorio 1: Modelado de Clases y Objetos", 5.0, 5.0, "18/07/2026", "Abstracción y modularidad impecable."),
                            new ActividadNota("Corto 1: Encapsulamiento y Constructores", 5.0, 5.0, "25/07/2026", "Excelente dominio conceptual."),
                            new ActividadNota("Laboratorio 2: Polimorfismo e Interfaces", 10.0, 10.0, "08/08/2026", "Diseño extensible y pruebas completas."),
                            new ActividadNota("Examen Parcial 1: Fundamentos POO", 20.0, 19.5, "22/08/2026", "Análisis sobresaliente de arquitectura."),
                            new ActividadNota("Laboratorio 3: Manejo de Excepciones y Colecciones", 10.0, 10.0, "05/09/2026", "Control de errores de nivel profesional."),
                            new ActividadNota("Proyecto Fase 1: Arquitectura de Software", 15.0, 14.5, "26/09/2026", "Patrones y documentación de excelencia."),
                            new ActividadNota("Examen Parcial 2: Patrones y Estructuras", 10.0, 9.0, "10/10/2026", "Aplicación perfecta de patrones."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4, "Sec. 10 (Matutina)", "Ing. Alejandro Morales", "#059669",
                    75.0, 72.0, 0.0, 72.0, "En curso",
                    List.of(
                            new ActividadNota("Práctica 1: Arquitectura de Registros x86-64", 5.0, 5.0, "20/07/2026", "Manejo óptimo de registros de 64 bits."),
                            new ActividadNota("Laboratorio 1: Aritmética y Banderas de Estado", 10.0, 10.0, "03/08/2026", "Instrucciones eficientes y compactas."),
                            new ActividadNota("Examen Parcial 1: Modos de Direccionamiento", 20.0, 19.5, "24/08/2026", "Manipulación precisa de la pila."),
                            new ActividadNota("Laboratorio 2: Manejo de la Pila y Subrutinas", 10.0, 10.0, "14/09/2026", "Convenio de llamada System V perfecto."),
                            new ActividadNota("Proyecto Assembler: Decodificador Binario", 15.0, 14.5, "28/09/2026", "Máximo rendimiento en ciclos de CPU."),
                            new ActividadNota("Examen Parcial 2: Interrupciones y BIOS", 15.0, 13.0, "12/10/2026", "Control riguroso de memoria e I/O."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA1004", "Cálculo I", 4, "Sec. 10 (Matutina)", "Dr. Roberto Chang", "#7c3aed",
                    70.0, 68.0, 0.0, 68.0, "En curso",
                    List.of(
                            new ActividadNota("Hoja de Trabajo 1: Límites y Continuidad", 5.0, 5.0, "22/07/2026", "Demostraciones delta-épsilon correctas."),
                            new ActividadNota("Examen Corto 1: Regla de la Cadena", 5.0, 5.0, "05/08/2026", "Desarrollo analítico riguroso."),
                            new ActividadNota("Examen Parcial 1: Derivadas y Reglas", 25.0, 24.5, "26/08/2026", "Puntaje casi perfecto en optimización."),
                            new ActividadNota("Hoja de Trabajo 2: Tasas Relacionadas", 10.0, 9.5, "16/09/2026", "Solución geométrica elegante."),
                            new ActividadNota("Examen Parcial 2: Integrales Definidas", 25.0, 24.0, "14/10/2026", "Métodos de integración impecables."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA2005", "Estadística y Probabilidad", 3, "Sec. 10 (Matutina)", "Inga. Claudia Morales", "#ea580c",
                    70.0, 67.5, 0.0, 67.5, "En curso",
                    List.of(
                            new ActividadNota("Taller 1: Análisis Exploratorio de Datos", 10.0, 10.0, "24/07/2026", "Interpretación estadística sobresaliente."),
                            new ActividadNota("Examen Parcial 1: Probabilidad Clásica y Bayes", 20.0, 19.5, "28/08/2026", "Modelado de eventos independientes exacto."),
                            new ActividadNota("Taller 2: Modelado con R y Python", 10.0, 10.0, "18/09/2026", "Script estructurado y visualización pulcra."),
                            new ActividadNota("Examen Parcial 2: Distribuciones Continuas", 20.0, 19.0, "16/10/2026", "Cálculo de percentiles y z-scores."),
                            new ActividadNota("Proyecto: Inferencia y Muestreo", 10.0, 9.0, "23/10/2026", "Intervalos de confianza robustos."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3, "Sec. 10 (Matutina)", "Dr. Fernando Morales", "#db2777",
                    75.0, 74.0, 0.0, 74.0, "En curso",
                    List.of(
                            new ActividadNota("Ensayo 1: Estado Constitucional y Democracia", 15.0, 15.0, "30/07/2026", "Postura crítica de gran altura académica."),
                            new ActividadNota("Debate 1: Ética Pública y Transparencia", 15.0, 15.0, "27/08/2026", "Liderazgo en debate y solidez conceptual."),
                            new ActividadNota("Examen Parcial 1: Marco Legal e Institucional", 20.0, 20.0, "24/09/2026", "Dominio pleno del ordenamiento jurídico."),
                            new ActividadNota("Fase 1: Diagnóstico de Iniciativa Comunitaria", 25.0, 24.0, "15/10/2026", "Propuesta de alto impacto y factibilidad."),
                            new ActividadNota("Presentación Final del Proyecto", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1001", "Guatemala en el Contexto Global", 3, "Sec. 10 (Matutina)", "Inga. Sofía Castillo", "#0d9488",
                    70.0, 69.0, 0.0, 69.0, "En curso",
                    List.of(
                            new ActividadNota("Lectura Crítica 1: Historia Económica", 10.0, 10.0, "28/07/2026", "Excelente marco de análisis comparativo."),
                            new ActividadNota("Examen Parcial 1: Tratados Comerciales", 20.0, 20.0, "01/09/2026", "Respuestas analíticas profundas."),
                            new ActividadNota("Estudio de Caso: Competitividad e Innovación", 15.0, 15.0, "22/09/2026", "Propuesta innovadora y viable."),
                            new ActividadNota("Examen Parcial 2: Retos Geopolíticos", 25.0, 24.0, "20/10/2026", "Visión global fundamentada en datos."),
                            new ActividadNota("Ensayo Final Integrador", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            return list;
        }

        if (carnet == 26710) {
            list.add(new NotaCurso("CC2008", "Programación Orientada a Objetos", 4, "Sec. 10 (Matutina)", "Ing. Carlos Mendoza", "#0284c7",
                    75.0, 51.0, 0.0, 51.0, "En curso",
                    List.of(
                            new ActividadNota("Laboratorio 1: Modelado de Clases y Objetos", 5.0, 3.5, "18/07/2026", "Faltó encapsular atributos privados."),
                            new ActividadNota("Corto 1: Encapsulamiento y Constructores", 5.0, 3.5, "25/07/2026", "Concepto regular de sobrecarga."),
                            new ActividadNota("Laboratorio 2: Polimorfismo e Interfaces", 10.0, 7.0, "08/08/2026", "Interfaz implementada parcialmente."),
                            new ActividadNota("Examen Parcial 1: Fundamentos POO", 20.0, 13.0, "22/08/2026", "Confusión en relación de agregación."),
                            new ActividadNota("Laboratorio 3: Manejo de Excepciones y Colecciones", 10.0, 7.0, "05/09/2026", "Faltó manejo de NullPointerException."),
                            new ActividadNota("Proyecto Fase 1: Arquitectura de Software", 15.0, 11.0, "26/09/2026", "Estructura funcional básica."),
                            new ActividadNota("Examen Parcial 2: Patrones y Estructuras", 10.0, 6.0, "10/10/2026", "Patrón Singleton incompleto."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4, "Sec. 10 (Matutina)", "Ing. Alejandro Morales", "#059669",
                    75.0, 45.0, 0.0, 45.0, "En curso",
                    List.of(
                            new ActividadNota("Práctica 1: Arquitectura de Registros x86-64", 5.0, 3.0, "20/07/2026", "Errores en sintaxis NASM."),
                            new ActividadNota("Laboratorio 1: Aritmética y Banderas de Estado", 10.0, 6.0, "03/08/2026", "Bandera de acarreo no considerada."),
                            new ActividadNota("Examen Parcial 1: Modos de Direccionamiento", 20.0, 12.0, "24/08/2026", "Desplazamientos de memoria incorrectos."),
                            new ActividadNota("Laboratorio 2: Manejo de la Pila y Subrutinas", 10.0, 6.5, "14/09/2026", "Desbalance en registros base."),
                            new ActividadNota("Proyecto Assembler: Decodificador Binario", 15.0, 9.5, "28/09/2026", "Lógica funcional con ciclos redundantes."),
                            new ActividadNota("Examen Parcial 2: Interrupciones y BIOS", 15.0, 8.0, "12/10/2026", "Confusión entre interrupciones 21h y 10h."),
                            new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA1004", "Cálculo I", 4, "Sec. 10 (Matutina)", "Dr. Roberto Chang", "#7c3aed",
                    70.0, 43.0, 0.0, 43.0, "En curso",
                    List.of(
                            new ActividadNota("Hoja de Trabajo 1: Límites y Continuidad", 5.0, 3.0, "22/07/2026", "Errores en indeterminaciones 0/0."),
                            new ActividadNota("Examen Corto 1: Regla de la Cadena", 5.0, 3.0, "05/08/2026", "Omisión de derivadas internas."),
                            new ActividadNota("Examen Parcial 1: Derivadas y Reglas", 25.0, 15.0, "26/08/2026", "Dificultad en diferenciación logarítmica."),
                            new ActividadNota("Hoja de Trabajo 2: Tasas Relacionadas", 10.0, 6.0, "16/09/2026", "Planteamiento con signos invertidos."),
                            new ActividadNota("Examen Parcial 2: Integrales Definidas", 25.0, 16.0, "14/10/2026", "Integración por partes con errores de signo."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("MA2005", "Estadística y Probabilidad", 3, "Sec. 10 (Matutina)", "Inga. Claudia Morales", "#ea580c",
                    70.0, 53.5, 0.0, 53.5, "En curso",
                    List.of(
                            new ActividadNota("Taller 1: Análisis Exploratorio de Datos", 10.0, 7.5, "24/07/2026", "Gráficos legibles sin leyendas."),
                            new ActividadNota("Examen Parcial 1: Probabilidad Clásica y Bayes", 20.0, 15.0, "28/08/2026", "Buena aplicación de teorema de Bayes."),
                            new ActividadNota("Taller 2: Modelado con R y Python", 10.0, 7.0, "18/09/2026", "Comentarios de código escasos."),
                            new ActividadNota("Examen Parcial 2: Distribuciones Continuas", 20.0, 16.0, "16/10/2026", "Cálculo de áreas normalizado regular."),
                            new ActividadNota("Proyecto: Inferencia y Muestreo", 10.0, 8.0, "23/10/2026", "Muestra representativa adecuada."),
                            new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3, "Sec. 10 (Matutina)", "Dr. Fernando Morales", "#db2777",
                    75.0, 58.0, 0.0, 58.0, "En curso",
                    List.of(
                            new ActividadNota("Ensayo 1: Estado Constitucional y Democracia", 15.0, 12.0, "30/07/2026", "Argumento descriptivo pero claro."),
                            new ActividadNota("Debate 1: Ética Pública y Transparencia", 15.0, 11.0, "27/08/2026", "Intervenciones oportunas."),
                            new ActividadNota("Examen Parcial 1: Marco Legal e Institucional", 20.0, 16.0, "24/09/2026", "Respuestas conceptuales acertadas."),
                            new ActividadNota("Fase 1: Diagnóstico de Iniciativa Comunitaria", 25.0, 19.0, "15/10/2026", "Problema delimitado correctamente."),
                            new ActividadNota("Presentación Final del Proyecto", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            list.add(new NotaCurso("SH1001", "Guatemala en el Contexto Global", 3, "Sec. 10 (Matutina)", "Inga. Sofía Castillo", "#0d9488",
                    70.0, 48.0, 0.0, 48.0, "En curso",
                    List.of(
                            new ActividadNota("Lectura Crítica 1: Historia Económica", 10.0, 7.0, "28/07/2026", "Resumen adecuado del contexto."),
                            new ActividadNota("Examen Parcial 1: Tratados Comerciales", 20.0, 13.0, "01/09/2026", "Respuestas breves sin suficiente análisis."),
                            new ActividadNota("Estudio de Caso: Competitividad e Innovación", 15.0, 11.0, "22/09/2026", "Datos económicos coherentes."),
                            new ActividadNota("Examen Parcial 2: Retos Geopolíticos", 25.0, 17.0, "20/10/2026", "Buen marco geográfico."),
                            new ActividadNota("Ensayo Final Integrador", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                    )
            ));
            return list;
        }

        list.add(new NotaCurso("CC2008", "Programación Orientada a Objetos", 4, "Sec. 10 (Matutina)", "Ing. Carlos Mendoza", "#0284c7",
                75.0, 65.0, 0.0, 65.0, "En curso",
                List.of(
                        new ActividadNota("Laboratorio 1: Modelado de Clases y Objetos", 5.0, 5.0, "18/07/2026", "Excelente modelado con paquetes."),
                        new ActividadNota("Corto 1: Encapsulamiento y Constructores", 5.0, 4.0, "25/07/2026", "Correcta visibilidad de métodos."),
                        new ActividadNota("Laboratorio 2: Polimorfismo e Interfaces", 10.0, 9.0, "08/08/2026", "Sobrecarga y ligadura dinámica clara."),
                        new ActividadNota("Examen Parcial 1: Fundamentos POO", 20.0, 17.0, "22/08/2026", "Buen análisis conceptual en diagramas."),
                        new ActividadNota("Laboratorio 3: Manejo de Excepciones y Colecciones", 10.0, 8.5, "05/09/2026", "Uso correcto de ArrayList y HashMap."),
                        new ActividadNota("Proyecto Fase 1: Arquitectura de Software", 15.0, 13.5, "26/09/2026", "Arquitectura en capas bien estructurada."),
                        new ActividadNota("Examen Parcial 2: Patrones y Estructuras", 10.0, 8.0, "10/10/2026", "Patrones Factory y DAO aplicados."),
                        new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("CC2006", "Organización y Lenguaje Ensamblador (Assembler)", 4, "Sec. 10 (Matutina)", "Ing. Alejandro Morales", "#059669",
                75.0, 48.5, 0.0, 48.5, "En curso",
                List.of(
                        new ActividadNota("Práctica 1: Arquitectura de Registros x86-64", 5.0, 4.0, "20/07/2026", "Uso correcto de rax, rbx y rcx."),
                        new ActividadNota("Laboratorio 1: Aritmética y Banderas de Estado", 10.0, 7.0, "03/08/2026", "Manejo adecuado de carry flag."),
                        new ActividadNota("Examen Parcial 1: Modos de Direccionamiento", 20.0, 13.0, "24/08/2026", "Buen uso de direccionamiento indirecto."),
                        new ActividadNota("Laboratorio 2: Manejo de la Pila y Subrutinas", 10.0, 7.0, "14/09/2026", "Retorno de función sin desborde."),
                        new ActividadNota("Proyecto Assembler: Decodificador Binario", 15.0, 10.0, "28/09/2026", "Decodificación funcional en consola."),
                        new ActividadNota("Examen Parcial 2: Interrupciones y BIOS", 15.0, 7.5, "12/10/2026", "Dificultad en manejo de punteros de video."),
                        new ActividadNota("Examen Final", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("MA1004", "Cálculo I", 4, "Sec. 10 (Matutina)", "Dr. Roberto Chang", "#7c3aed",
                70.0, 38.0, 0.0, 38.0, "En curso",
                List.of(
                        new ActividadNota("Hoja de Trabajo 1: Límites y Continuidad", 5.0, 3.0, "22/07/2026", "Límites laterales resueltos."),
                        new ActividadNota("Examen Corto 1: Regla de la Cadena", 5.0, 2.5, "05/08/2026", "Faltó simplificar expresiones."),
                        new ActividadNota("Examen Parcial 1: Derivadas y Reglas", 25.0, 13.0, "26/08/2026", "Derivadas trigonométricas con errores."),
                        new ActividadNota("Hoja de Trabajo 2: Tasas Relacionadas", 10.0, 5.0, "16/09/2026", "Ecuación diferencial mal planteada."),
                        new ActividadNota("Examen Parcial 2: Integrales Definidas", 25.0, 14.5, "14/10/2026", "Sustitución simple realizada correctamente."),
                        new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("MA2005", "Estadística y Probabilidad", 3, "Sec. 10 (Matutina)", "Inga. Claudia Morales", "#ea580c",
                70.0, 57.0, 0.0, 57.0, "En curso",
                List.of(
                        new ActividadNota("Taller 1: Análisis Exploratorio de Datos", 10.0, 8.5, "24/07/2026", "Histogramas bien calculados."),
                        new ActividadNota("Examen Parcial 1: Probabilidad Clásica y Bayes", 20.0, 17.0, "28/08/2026", "Dominio de probabilidad condicional."),
                        new ActividadNota("Taller 2: Modelado con R y Python", 10.0, 8.0, "18/09/2026", "Dataframe manipulado con destreza."),
                        new ActividadNota("Examen Parcial 2: Distribuciones Continuas", 20.0, 16.5, "16/10/2026", "Correcto uso de tablas de distribución Z."),
                        new ActividadNota("Proyecto: Inferencia y Muestreo", 10.0, 7.0, "23/10/2026", "Pruebas de hipótesis con p-value claro."),
                        new ActividadNota("Examen Final", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("SH1002", "Pensamiento Ciudadano (Ciudadanía)", 3, "Sec. 10 (Matutina)", "Dr. Fernando Morales", "#db2777",
                75.0, 68.0, 0.0, 68.0, "En curso",
                List.of(
                        new ActividadNota("Ensayo 1: Estado Constitucional y Democracia", 15.0, 14.0, "30/07/2026", "Gran profundidad de análisis."),
                        new ActividadNota("Debate 1: Ética Pública y Transparencia", 15.0, 13.5, "27/08/2026", "Argumentos bien formulados."),
                        new ActividadNota("Examen Parcial 1: Marco Legal e Institucional", 20.0, 18.0, "24/09/2026", "Respuestas teóricas precisas."),
                        new ActividadNota("Fase 1: Diagnóstico de Iniciativa Comunitaria", 25.0, 22.5, "15/10/2026", "Propuesta estructurada y con fuentes."),
                        new ActividadNota("Presentación Final del Proyecto", 25.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));
        list.add(new NotaCurso("SH1001", "Guatemala en el Contexto Global", 3, "Sec. 10 (Matutina)", "Inga. Sofía Castillo", "#0d9488",
                70.0, 51.5, 0.0, 51.5, "En curso",
                List.of(
                        new ActividadNota("Lectura Crítica 1: Historia Económica", 10.0, 7.5, "28/07/2026", "Buen resumen de hechos clave."),
                        new ActividadNota("Examen Parcial 1: Tratados Comerciales", 20.0, 14.0, "01/09/2026", "Respuestas analíticas correctas."),
                        new ActividadNota("Estudio de Caso: Competitividad e Innovación", 15.0, 11.5, "22/09/2026", "Datos económicos pertinentes."),
                        new ActividadNota("Examen Parcial 2: Retos Geopolíticos", 25.0, 18.5, "20/10/2026", "Visión amplia de comercio exterior."),
                        new ActividadNota("Ensayo Final Integrador", 30.0, 0.0, "Pendiente", "Por evaluar al cierre del ciclo.")
                )
        ));

        return list;
    }
    public List<SemestreHistorial> obtenerSemestres(int carnetFinal) {
        List<SemestreHistorial> semestres = new ArrayList<>();

        if (carnetFinal == 26547) {
            List<CursoHistorial> s1Cursos = List.of(
                    new CursoHistorial("CC1001", "Introducción a la Programación", 4, 88.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("MA1001", "Precálculo y Álgebra Lineal", 4, 84.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("FS1001", "Física I (Mecánica Clásica)", 4, 82.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("CO1001", "Comunicación y Redacción Técnica", 3, 90.0, "Aprobado", "Primer Semestre 2025"),
                    new CursoHistorial("QU1001", "Química General para Computación", 3, 85.0, "Aprobado", "Primer Semestre 2025")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2025", 85.8, 18, s1Cursos));

            List<CursoHistorial> s2Cursos = List.of(
                    new CursoHistorial("CC1002", "Algoritmos y Estructuras de Datos I", 4, 91.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("MA1002", "Cálculo Diferencial", 4, 86.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("FS1002", "Física II (Electricidad y Magnetismo)", 4, 83.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("CC1003", "Lógica Computacional y Discretas", 3, 89.0, "Aprobado", "Segundo Semestre 2025"),
                    new CursoHistorial("ME1001", "Metodología de la Investigación", 3, 87.0, "Aprobado", "Segundo Semestre 2025")
            );
            semestres.add(new SemestreHistorial("Segundo Semestre 2025", 87.3, 18, s2Cursos));

            List<CursoHistorial> s3Cursos = List.of(
                    new CursoHistorial("CC2001", "Algoritmos y Estructuras de Datos II", 4, 92.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("MA1003", "Cálculo Integral", 4, 85.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CC2002", "Arquitectura y Organización de Computadores I", 4, 88.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CC2004", "Sistemas Operativos (Fundamentos)", 4, 86.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("ET1001", "Ética en la Era de la Información", 3, 93.0, "Aprobado", "Primer Semestre 2026")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2026", 88.7, 19, s3Cursos));
        } else if (carnetFinal == 26000) {
            List<CursoHistorial> s1Cursos = List.of(
                    new CursoHistorial("CC1001", "Introducción a la Programación", 4, 96.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("MA1001", "Precálculo y Álgebra Lineal", 4, 94.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("FS1001", "Física I (Mecánica Clásica)", 4, 92.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CO1001", "Comunicación y Redacción Técnica", 3, 98.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("QU1001", "Química General para Computación", 3, 96.0, "Aprobado", "Primer Semestre 2026")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2026", 95.2, 18, s1Cursos));
        } else {
            List<CursoHistorial> s1Cursos = List.of(
                    new CursoHistorial("CC1001", "Introducción a la Programación", 4, 72.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("MA1001", "Precálculo y Álgebra Lineal", 4, 68.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("FS1001", "Física I (Mecánica Clásica)", 4, 65.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("CO1001", "Comunicación y Redacción Técnica", 3, 76.0, "Aprobado", "Primer Semestre 2026"),
                    new CursoHistorial("QU1001", "Química General para Computación", 3, 74.0, "Aprobado", "Primer Semestre 2026")
            );
            semestres.add(new SemestreHistorial("Primer Semestre 2026", 71.0, 18, s1Cursos));
        }

        return List.copyOf(semestres);
    }
}

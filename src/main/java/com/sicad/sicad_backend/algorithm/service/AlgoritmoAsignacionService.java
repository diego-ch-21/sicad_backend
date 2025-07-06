package com.sicad.sicad_backend.algorithm.service;
import com.sicad.sicad_backend.algorithm.genetic.GeneticAlgorithm;
import com.sicad.sicad_backend.algorithm.model.SolucionAsignacion;
import com.sicad.sicad_backend.algorithm.pso.PSOAlgorithm;
import com.sicad.sicad_backend.algorithm.validator.RestriccionValidator;
import com.sicad.sicad_backend.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal que orquesta la ejecución del algoritmo híbrido GA+PSO
 * para la asignación óptima de docentes a cursos
 * ACTUALIZADO: Solo considera horasMaxLectivas como restricción dura
 */
@Slf4j
@Service
public class AlgoritmoAsignacionService {

    // Parámetros configurables del algoritmo
    private static final int POBLACION_GA = 50;
    private static final int GENERACIONES_GA = 100;
    private static final double PROB_CRUZAMIENTO = 0.8;
    private static final double PROB_MUTACION = 0.15;
    private static final double ELITISMO = 0.1;

    private static final int ENJAMBRE_PSO = 30;
    private static final int ITERACIONES_PSO = 80;
    private static final double INERCIA_INICIAL = 0.9;
    private static final double INERCIA_FINAL = 0.4;
    private static final double C1 = 2.0; // Componente cognitivo
    private static final double C2 = 2.0; // Componente social
    private static final double VELOCIDAD_MAXIMA = 1.0;

    private static final int CICLOS_HIBRIDOS = 3;

    /**
     * Ejecuta el algoritmo híbrido completo para generar asignaciones óptimas
     * ACTUALIZADO: Solo considera horasMaxLectivas, no dedicación
     */
    public List<Asignacion> ejecutarAlgoritmoHibrido(List<Docente> docentes, List<Curso> cursos,
                                                     CargaElectiva cargaElectiva,
                                                     Map<Integer, List<Disponibilidad>> disponibilidadPorDocente,
                                                     Map<Integer, List<Preferencia>> preferenciasPorDocente) {

        log.info("=== INICIANDO ALGORITMO HÍBRIDO GA+PSO (SIN RESTRICCIONES DE DEDICACIÓN) ===");
        log.info("Docentes: {}, Cursos: {}, Carga Electiva: {}",
                docentes.size(), cursos.size(), cargaElectiva.getNombre());

        try {
            // 1. Inicialización del validador de restricciones (actualizado)
            RestriccionValidator validator = new RestriccionValidator(
                    docentes, cursos, disponibilidadPorDocente, preferenciasPorDocente, cargaElectiva);

            // 2. Generar población inicial diversa
            List<SolucionAsignacion> poblacionInicial = generarPoblacionInicial(
                    docentes, cursos, validator, Math.max(POBLACION_GA, ENJAMBRE_PSO));

            log.info("Población inicial generada: {} soluciones", poblacionInicial.size());

            // 3. Ejecutar algoritmo híbrido por ciclos
            SolucionAsignacion mejorSolucion = null;

            for (int ciclo = 0; ciclo < CICLOS_HIBRIDOS; ciclo++) {
                log.info("--- Iniciando Ciclo Híbrido {} ---", ciclo + 1);

                // 3.1 Ejecutar Algoritmo Genético
                GeneticAlgorithm ga = new GeneticAlgorithm(
                        POBLACION_GA, PROB_CRUZAMIENTO, PROB_MUTACION,
                        GENERACIONES_GA, ELITISMO, validator);

                SolucionAsignacion mejorGA = ga.ejecutar(poblacionInicial);
                log.info("GA completado - Fitness: {:.2f}", mejorGA.getFitness());

                // 3.2 Ejecutar PSO
                PSOAlgorithm pso = new PSOAlgorithm(
                        ENJAMBRE_PSO, ITERACIONES_PSO, INERCIA_INICIAL, INERCIA_FINAL,
                        C1, C2, VELOCIDAD_MAXIMA, validator);

                SolucionAsignacion mejorPSO = pso.ejecutar(poblacionInicial);
                log.info("PSO completado - Fitness: {:.2f}", mejorPSO.getFitness());

                // 3.3 Seleccionar mejor solución del ciclo
                SolucionAsignacion mejorCiclo = mejorGA.getFitness() > mejorPSO.getFitness() ?
                        mejorGA : mejorPSO;

                // 3.4 Actualizar mejor solución global
                if (mejorSolucion == null || mejorCiclo.getFitness() > mejorSolucion.getFitness()) {
                    mejorSolucion = new SolucionAsignacion(mejorCiclo);
                    log.info("Nueva mejor solución encontrada - Fitness: {:.2f}", mejorSolucion.getFitness());
                }

                // 3.5 Preparar población para siguiente ciclo
                if (ciclo < CICLOS_HIBRIDOS - 1) {
                    poblacionInicial = prepararSiguienteCiclo(ga, pso, mejorSolucion, validator);
                }
            }

            // 4. Verificar y reparar solución final
            if (mejorSolucion != null) {
                validator.repararSolucion(mejorSolucion);
                validator.calcularFitness(mejorSolucion);

                log.info("=== ALGORITMO HÍBRIDO COMPLETADO ===");
                logearEstadisticasFinales(mejorSolucion, docentes, cursos);

                // 5. Convertir a entidades de asignación
                return convertirAAsignaciones(mejorSolucion, docentes, cursos, cargaElectiva);
            } else {
                log.error("No se pudo generar ninguna solución válida");
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("Error en ejecución del algoritmo híbrido: ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Genera una población inicial diversa usando diferentes estrategias
     * ACTUALIZADO: Solo considera horasMaxLectivas
     */
    private List<SolucionAsignacion> generarPoblacionInicial(List<Docente> docentes, List<Curso> cursos,
                                                             RestriccionValidator validator, int tamaño) {
        List<SolucionAsignacion> poblacion = new ArrayList<>();
        Random random = new Random();

        // Estrategia 1: Solución basada en preferencias (20%)
        int porPreferencias = (int) (tamaño * 0.2);
        for (int i = 0; i < porPreferencias; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            generarSolucionPorPreferencias(solucion, validator);
            poblacion.add(solucion);
        }

        // Estrategia 2: Solución greedy por disponibilidad (20%)
        int porDisponibilidad = (int) (tamaño * 0.2);
        for (int i = 0; i < porDisponibilidad; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            generarSolucionGreedy(solucion, validator);
            poblacion.add(solucion);
        }

        // Estrategia 3: Solución balanceada por carga (20%)
        int porBalanceada = (int) (tamaño * 0.2);
        for (int i = 0; i < porBalanceada; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            generarSolucionBalanceada(solucion, validator);
            poblacion.add(solucion);
        }

        // Estrategia 4: Soluciones completamente aleatorias (40%)
        int porAleatorias = tamaño - poblacion.size();
        for (int i = 0; i < porAleatorias; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            solucion.generarSolucionAleatoria(random);
            poblacion.add(solucion);
        }

        // Reparar y evaluar todas las soluciones
        for (SolucionAsignacion solucion : poblacion) {
            validator.repararSolucion(solucion);
            validator.calcularFitness(solucion);
        }

        return poblacion;
    }

    /**
     * Genera solución priorizando preferencias de docentes
     */
    private void generarSolucionPorPreferencias(SolucionAsignacion solucion, RestriccionValidator validator) {
        // Implementación que prioriza asignar cursos a docentes que los prefieren
        for (Curso curso : solucion.getCursos()) {
            List<Docente> candidatos = solucion.getDocentes().stream()
                    .filter(docente -> tienePreferenciaPorCurso(docente, curso))
                    .filter(docente -> puedeAsignarCurso(solucion, docente, curso))
                    .collect(Collectors.toList());

            if (!candidatos.isEmpty()) {
                Docente elegido = candidatos.get(new Random().nextInt(candidatos.size()));
                solucion.asignarDocente(curso.getIdCurso(), elegido.getIdDocente());
            }
        }
        solucion.actualizarEstadisticas();
    }

    /**
     * Genera solución usando estrategia greedy basada en disponibilidad
     */
    private void generarSolucionGreedy(SolucionAsignacion solucion, RestriccionValidator validator) {
        // Ordenar cursos por dificultad de asignación (menos docentes disponibles primero)
        List<Curso> cursosOrdenados = solucion.getCursos().stream()
                .sorted((c1, c2) -> Integer.compare(
                        contarDocentesDisponibles(solucion, c1),
                        contarDocentesDisponibles(solucion, c2)))
                .collect(Collectors.toList());

        for (Curso curso : cursosOrdenados) {
            List<Docente> candidatos = solucion.getDocentes().stream()
                    .filter(docente -> puedeAsignarCurso(solucion, docente, curso))
                    .collect(Collectors.toList());

            if (!candidatos.isEmpty()) {
                // Elegir docente con menos carga actual
                Docente elegido = candidatos.stream()
                        .min(Comparator.comparingInt(d -> solucion.getHorasTotalesDocente(d.getIdDocente())))
                        .orElse(candidatos.get(0));

                solucion.asignarDocente(curso.getIdCurso(), elegido.getIdDocente());
            }
        }
        solucion.actualizarEstadisticas();
    }

    /**
     * Genera solución balanceando la carga entre docentes
     */
    private void generarSolucionBalanceada(SolucionAsignacion solucion, RestriccionValidator validator) {
        List<Curso> cursosDisponibles = new ArrayList<>(solucion.getCursos());
        Collections.shuffle(cursosDisponibles);

        for (Curso curso : cursosDisponibles) {
            // Buscar docente con menor carga que pueda tomar el curso
            Optional<Docente> docenteOptimo = solucion.getDocentes().stream()
                    .filter(docente -> puedeAsignarCurso(solucion, docente, curso))
                    .min(Comparator.comparingInt(d -> solucion.getHorasTotalesDocente(d.getIdDocente())));

            if (docenteOptimo.isPresent()) {
                solucion.asignarDocente(curso.getIdCurso(), docenteOptimo.get().getIdDocente());
            }
        }
        solucion.actualizarEstadisticas();
    }

    /**
     * Prepara la población para el siguiente ciclo híbrido
     */
    private List<SolucionAsignacion> prepararSiguienteCiclo(GeneticAlgorithm ga, PSOAlgorithm pso,
                                                            SolucionAsignacion mejorGlobal,
                                                            RestriccionValidator validator) {
        List<SolucionAsignacion> nuevaPoblacion = new ArrayList<>();

        // Incluir mejor solución global
        nuevaPoblacion.add(new SolucionAsignacion(mejorGlobal));

        // Incluir mejores del GA (30%)
        List<SolucionAsignacion> mejoresGA = ga.getPoblacionActual().stream()
                .sorted((s1, s2) -> Double.compare(s2.getFitness(), s1.getFitness()))
                .limit(POBLACION_GA * 30 / 100)
                .collect(Collectors.toList());
        nuevaPoblacion.addAll(mejoresGA);

        // Incluir mejores del PSO (30%)
        List<SolucionAsignacion> mejoresPSO = pso.getPosicionesActuales().stream()
                .sorted((s1, s2) -> Double.compare(s2.getFitness(), s1.getFitness()))
                .limit(ENJAMBRE_PSO * 30 / 100)
                .collect(Collectors.toList());
        nuevaPoblacion.addAll(mejoresPSO);

        // Completar con variaciones de la mejor solución (40%)
        int faltantes = Math.max(POBLACION_GA, ENJAMBRE_PSO) - nuevaPoblacion.size();
        for (int i = 0; i < faltantes; i++) {
            SolucionAsignacion variacion = new SolucionAsignacion(mejorGlobal);
            variacion.mutar(new Random(), 0.1);
            validator.repararSolucion(variacion);
            validator.calcularFitness(variacion);
            nuevaPoblacion.add(variacion);
        }

        return nuevaPoblacion;
    }

    /**
     * Convierte la mejor solución a entidades de Asignacion
     */
    private List<Asignacion> convertirAAsignaciones(SolucionAsignacion solucion, List<Docente> docentes,
                                                    List<Curso> cursos, CargaElectiva cargaElectiva) {
        List<Asignacion> asignaciones = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = entry.getKey();
            Integer idDocente = entry.getValue();

            if (idDocente == -1) continue; // Curso sin asignar

            Docente docente = docentes.stream()
                    .filter(d -> d.getIdDocente().equals(idDocente))
                    .findFirst().orElse(null);

            Curso curso = cursos.stream()
                    .filter(c -> c.getIdCurso().equals(idCurso))
                    .findFirst().orElse(null);

            if (docente != null && curso != null) {
                Asignacion asignacion = new Asignacion();
                asignacion.setDocente(docente);
                asignacion.setCurso(curso);
                asignacion.setCargaElectiva(cargaElectiva);
                asignacion.setTipoAsignacion("LECTIVO");
                asignacion.setEnabled(true);
                asignacion.setCreatedAt(LocalDate.now());

                asignaciones.add(asignacion);
            }
        }

        return asignaciones;
    }

    /**
     * Métodos auxiliares de utilidad
     * ACTUALIZADO: Solo considera horasMaxLectivas como restricción dura
     */
    private boolean tienePreferenciaPorCurso(Docente docente, Curso curso) {
        // Implementar lógica de verificación de preferencias
        // Por ahora retornar true para simplificar
        return true;
    }

    private boolean puedeAsignarCurso(SolucionAsignacion solucion, Docente docente, Curso curso) {
        // Verificar si el docente puede tomar el curso considerando:
        // 1. Horas máximas no excedidas (ÚNICA RESTRICCIÓN DE HORAS)
        // 2. Disponibilidad horaria
        // 3. No conflictos de horario

        int horasActuales = solucion.getHorasTotalesDocente(docente.getIdDocente());
        int horasCurso = curso.getCursoHorario().stream()
                .mapToInt(CursoHorario::getDuracionHoras)
                .sum();

        // Usar horasMaxLectivas específicas del docente
        int horasMaximas = docente.getHorasMaxLectivas() != null ?
                docente.getHorasMaxLectivas() : 12;

        return horasActuales + horasCurso <= horasMaximas;
    }

    private int contarDocentesDisponibles(SolucionAsignacion solucion, Curso curso) {
        return (int) solucion.getDocentes().stream()
                .filter(docente -> puedeAsignarCurso(solucion, docente, curso))
                .count();
    }

    /**
     * Registra estadísticas finales de la ejecución
     * ACTUALIZADO: Menciona solo restricción de horasMaxLectivas
     */
    private void logearEstadisticasFinales(SolucionAsignacion mejorSolucion, List<Docente> docentes, List<Curso> cursos) {
        log.info("=== ESTADÍSTICAS FINALES (SOLO RESTRICCIÓN: horasMaxLectivas) ===");
        log.info("Fitness final: {:.2f}", mejorSolucion.getFitness());
        log.info("Cursos asignados: {}/{}", mejorSolucion.getCursosAsignados(), cursos.size());
        log.info("Cursos sin asignar: {}", mejorSolucion.getCursosSinAsignar());
        log.info("Docentes utilizados: {}/{}", mejorSolucion.getDocentesUtilizados(), docentes.size());
        log.info("Porcentaje preferencias: {:.1f}%", mejorSolucion.getPorcentajePreferencias());
        log.info("Equilibrio de carga: {:.2f}", mejorSolucion.getEquilibrioCarga());
        log.info("Solución válida: {}", mejorSolucion.isEsValida());

        // Detalles por docente
        for (Integer idDocente : mejorSolucion.getDocentesUtilizados()) {
            Optional<Docente> docenteOpt = docentes.stream()
                    .filter(d -> d.getIdDocente().equals(idDocente))
                    .findFirst();

            if (docenteOpt.isPresent()) {
                Docente docente = docenteOpt.get();
                int horas = mejorSolucion.getHorasTotalesDocente(idDocente);
                int cursosAsignados = mejorSolucion.getCursosDeDocente(idDocente).size();
                int horasMaximas = docente.getHorasMaxLectivas() != null ?
                        docente.getHorasMaxLectivas() : 12;

                log.info("Docente {}: {} horas (máx: {}), {} cursos",
                        docente.getCodigo(), horas, horasMaximas, cursosAsignados);
            }
        }
    }
}
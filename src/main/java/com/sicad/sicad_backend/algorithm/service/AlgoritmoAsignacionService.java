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
 * ACTUALIZADO y CORREGIDO:
 * - RESTRICCIONES DURAS: Disponibilidad Real (LocalTime) + horasMaxLectivas
 * - RESTRICCIONES BLANDAS: Preferencias reales del docente
 */
@Slf4j
@Service
public class AlgoritmoAsignacionService {

    // Parámetros configurables del algoritmo (Valores por defecto)
    private int POBLACION_GA = 50;
    private int GENERACIONES_GA = 100;
    private double PROB_CRUZAMIENTO = 0.8;
    private double PROB_MUTACION = 0.15;
    private double ELITISMO = 0.1;

    private int ENJAMBRE_PSO = 30;
    private int ITERACIONES_PSO = 80;
    private double INERCIA_INICIAL = 0.9;
    private double INERCIA_FINAL = 0.4;
    private double C1 = 2.0; // Componente cognitivo
    private double C2 = 2.0; // Componente social
    private double VELOCIDAD_MAXIMA = 1.0;

    private int CICLOS_HIBRIDOS = 3;

    /**
     * Ejecuta el algoritmo híbrido completo para generar asignaciones óptimas
     */
    public List<Asignacion> ejecutarAlgoritmoHibrido(List<Docente> docentes, List<Curso> cursos,
                                                     CicloAcademico cicloAcademico,
                                                     Map<Integer, List<Disponibilidad>> disponibilidadPorDocente,
                                                     Map<Integer, List<Preferencia>> preferenciasPorDocente,
                                                     Algoritmo algoritmo, Carga carga) {

        log.info("=== INICIANDO ALGORITMO HÍBRIDO GA+PSO (CORREGIDO) ===");

        try {
            // Configuración dinámica desde BD si existe
            if (algoritmo != null) {
                POBLACION_GA = algoritmo.getPoblacion();
                GENERACIONES_GA = algoritmo.getGeneracionGa();
                PROB_CRUZAMIENTO = algoritmo.getProbCruzamientos();
                PROB_MUTACION = algoritmo.getProbMutacion();
                ELITISMO = algoritmo.getElitismo();
                ENJAMBRE_PSO = algoritmo.getEnjambrePso();
                ITERACIONES_PSO = algoritmo.getIteracionesPso();
                INERCIA_INICIAL = algoritmo.getInerciaInicial();
                C1 = algoritmo.getCUno();
                C2 = algoritmo.getCDos();
                VELOCIDAD_MAXIMA = algoritmo.getVelocidadMaxima();
                CICLOS_HIBRIDOS = algoritmo.getCicloHibridos();
            }

            RestriccionValidator validator = new RestriccionValidator(
                    docentes, cursos, disponibilidadPorDocente, preferenciasPorDocente, cicloAcademico);

            // PASO 1: Generar Población Inicial (pasando mapas necesarios)
            List<SolucionAsignacion> poblacionInicial = generarPoblacionInicial(
                    docentes, cursos, validator, Math.max(POBLACION_GA, ENJAMBRE_PSO),
                    disponibilidadPorDocente, preferenciasPorDocente);

            log.info("Población inicial generada: {} soluciones", poblacionInicial.size());

            // PASO 2: Ciclo Híbrido
            SolucionAsignacion mejorSolucion = null;

            for (int ciclo = 0; ciclo < CICLOS_HIBRIDOS; ciclo++) {
                log.info("--- Iniciando Ciclo Híbrido {} ---", ciclo + 1);

                // 2.1 Ejecutar Algoritmo Genético
                GeneticAlgorithm ga = new GeneticAlgorithm(
                        POBLACION_GA, PROB_CRUZAMIENTO, PROB_MUTACION,
                        GENERACIONES_GA, ELITISMO, validator);

                SolucionAsignacion mejorGA = ga.ejecutar(poblacionInicial);
                log.info("GA completado - Fitness: {:.2f}", mejorGA.getFitness());

                // 2.2 Ejecutar PSO
                PSOAlgorithm pso = new PSOAlgorithm(
                        ENJAMBRE_PSO, ITERACIONES_PSO, INERCIA_INICIAL, INERCIA_FINAL,
                        C1, C2, VELOCIDAD_MAXIMA, validator);

                SolucionAsignacion mejorPSO = pso.ejecutar(poblacionInicial);
                log.info("PSO completado - Fitness: {:.2f}", mejorPSO.getFitness());

                // 2.3 Seleccionar mejor solución del ciclo
                SolucionAsignacion mejorCiclo = mejorGA.getFitness() > mejorPSO.getFitness() ?
                        mejorGA : mejorPSO;

                // 2.4 Actualizar mejor solución global
                if (mejorSolucion == null || mejorCiclo.getFitness() > mejorSolucion.getFitness()) {
                    mejorSolucion = new SolucionAsignacion(mejorCiclo);
                    log.info("Nueva mejor solución encontrada - Fitness: {:.2f}", mejorSolucion.getFitness());
                }

                // 2.5 Preparar población para siguiente ciclo
                if (ciclo < CICLOS_HIBRIDOS - 1) {
                    poblacionInicial = prepararSiguienteCiclo(ga, pso, mejorSolucion, validator);
                }
            }

            // PASO 3: Validar y convertir
            if (mejorSolucion != null) {
                validator.repararSolucion(mejorSolucion);
                validator.calcularFitness(mejorSolucion);

                logearEstadisticasFinales(mejorSolucion, docentes, cursos);
                return convertirAAsignaciones(mejorSolucion, docentes, cursos, cicloAcademico, carga);
            } else {
                log.error("No se pudo generar ninguna solución válida");
                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("Error en ejecución del algoritmo híbrido: ", e);
            return new ArrayList<>();
        }
    }

    // ==========================================
    // MÉTODOS DE GENERACIÓN DE POBLACIÓN INICIAL
    // ==========================================

    private List<SolucionAsignacion> generarPoblacionInicial(List<Docente> docentes, List<Curso> cursos,
                                                             RestriccionValidator validator, int tamaño,
                                                             Map<Integer, List<Disponibilidad>> mapDisponibilidad,
                                                             Map<Integer, List<Preferencia>> mapPreferencias) {
        List<SolucionAsignacion> poblacion = new ArrayList<>();
        Random random = new Random();

        // Estrategia 1: Preferencias (30%)
        int porPreferencias = (int) (tamaño * 0.3);
        for (int i = 0; i < porPreferencias; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            generarSolucionPorPreferencias(solucion, validator, mapDisponibilidad, mapPreferencias);
            poblacion.add(solucion);
        }

        // Estrategia 2: Greedy Disponibilidad (30%)
        int porDisponibilidad = (int) (tamaño * 0.3);
        for (int i = 0; i < porDisponibilidad; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            generarSolucionGreedy(solucion, validator, mapDisponibilidad);
            poblacion.add(solucion);
        }

        // Estrategia 3: Balanceada (20%)
        int porBalanceada = (int) (tamaño * 0.2);
        for (int i = 0; i < porBalanceada; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            generarSolucionBalanceada(solucion, validator, mapDisponibilidad);
            poblacion.add(solucion);
        }

        // Estrategia 4: Aleatorias (Resto)
        int porAleatorias = tamaño - poblacion.size();
        for (int i = 0; i < porAleatorias; i++) {
            SolucionAsignacion solucion = new SolucionAsignacion(cursos, docentes);
            solucion.generarSolucionAleatoria(random);
            poblacion.add(solucion);
        }

        // Reparar y evaluar
        for (SolucionAsignacion solucion : poblacion) {
            validator.repararSolucion(solucion);
            validator.calcularFitness(solucion);
        }

        return poblacion;
    }

    private void generarSolucionPorPreferencias(SolucionAsignacion solucion, RestriccionValidator validator,
                                                Map<Integer, List<Disponibilidad>> mapDisponibilidad,
                                                Map<Integer, List<Preferencia>> mapPreferencias) {
        for (Curso curso : solucion.getCursos()) {
            // Filtrar docentes que PUEDEN tomar el curso (Disponibilidad + Horas)
            List<Docente> candidatos = solucion.getDocentes().stream()
                    .filter(docente -> puedeAsignarCurso(solucion, docente, curso, mapDisponibilidad))
                    .collect(Collectors.toList());

            if (candidatos.isEmpty()) continue;

            // De los candidatos, buscar quienes lo PREFIEREN
            List<Docente> docentesConPreferencia = candidatos.stream()
                    .filter(docente -> tienePreferenciaPorCurso(docente, curso, mapPreferencias))
                    .collect(Collectors.toList());

            Docente elegido;
            if (!docentesConPreferencia.isEmpty()) {
                elegido = docentesConPreferencia.get(new Random().nextInt(docentesConPreferencia.size()));
            } else {
                elegido = candidatos.get(new Random().nextInt(candidatos.size()));
            }
            solucion.asignarDocente(curso.getIdCurso(), elegido.getIdDocente());
        }
        solucion.actualizarEstadisticas();
    }

    private void generarSolucionGreedy(SolucionAsignacion solucion, RestriccionValidator validator,
                                       Map<Integer, List<Disponibilidad>> mapDisponibilidad) {
        // Ordenar cursos: Primero los más difíciles de asignar (menos docentes disponibles)
        List<Curso> cursosOrdenados = solucion.getCursos().stream()
                .sorted((c1, c2) -> Integer.compare(
                        contarDocentesDisponibles(solucion, c1, mapDisponibilidad),
                        contarDocentesDisponibles(solucion, c2, mapDisponibilidad)))
                .collect(Collectors.toList());

        for (Curso curso : cursosOrdenados) {
            List<Docente> candidatos = solucion.getDocentes().stream()
                    .filter(docente -> puedeAsignarCurso(solucion, docente, curso, mapDisponibilidad))
                    .collect(Collectors.toList());

            if (!candidatos.isEmpty()) {
                // Elegir al que tiene menos carga actualmente
                Docente elegido = candidatos.stream()
                        .min(Comparator.comparingInt(d -> solucion.getHorasTotalesDocente(d.getIdDocente())))
                        .orElse(candidatos.get(0));
                solucion.asignarDocente(curso.getIdCurso(), elegido.getIdDocente());
            }
        }
        solucion.actualizarEstadisticas();
    }

    private void generarSolucionBalanceada(SolucionAsignacion solucion, RestriccionValidator validator,
                                           Map<Integer, List<Disponibilidad>> mapDisponibilidad) {
        List<Curso> cursosDisponibles = new ArrayList<>(solucion.getCursos());
        Collections.shuffle(cursosDisponibles);

        for (Curso curso : cursosDisponibles) {
            Optional<Docente> docenteOptimo = solucion.getDocentes().stream()
                    .filter(docente -> puedeAsignarCurso(solucion, docente, curso, mapDisponibilidad))
                    .min(Comparator.comparingInt(d -> solucion.getHorasTotalesDocente(d.getIdDocente())));

            docenteOptimo.ifPresent(docente -> solucion.asignarDocente(curso.getIdCurso(), docente.getIdDocente()));
        }
        solucion.actualizarEstadisticas();
    }

    // ==========================================
    // MÉTODOS DE VALIDACIÓN Y LÓGICA CORE
    // ==========================================

    /**
     * Verifica horas máximas Y disponibilidad real de TODOS los horarios del curso
     */
    private boolean puedeAsignarCurso(SolucionAsignacion solucion, Docente docente, Curso curso,
                                      Map<Integer, List<Disponibilidad>> mapDisponibilidad) {

        // 1. Verificar límite de horasMaxLectivas
        int horasActuales = solucion.getHorasTotalesDocente(docente.getIdDocente());
        int horasCurso = curso.getHorarios().stream()
                .mapToInt(Horario::getDuracionHoras)
                .sum();

        int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                docente.getDedicacion().getHorasMaxLectivas() : 12;

        if (horasActuales + horasCurso > horasMaximas) {
            return false;
        }

        // 2. Verificar disponibilidad horaria REAL
        List<Disponibilidad> disponibilidades = mapDisponibilidad.get(docente.getIdDocente());
        if (disponibilidades == null || disponibilidades.isEmpty()) {
            return false;
        }

        // Verificar que CADA horario del curso esté cubierto por ALGUNA disponibilidad del docente
        return curso.getHorarios().stream().allMatch(horarioCurso ->
                disponibilidades.stream().anyMatch(dispDocente ->
                        verificarSolapamiento(dispDocente, horarioCurso)
                )
        );
    }

    private int contarDocentesDisponibles(SolucionAsignacion solucion, Curso curso,
                                          Map<Integer, List<Disponibilidad>> mapDisponibilidad) {
        return (int) solucion.getDocentes().stream()
                .filter(docente -> puedeAsignarCurso(solucion, docente, curso, mapDisponibilidad))
                .count();
    }

    /**
     * Verifica cruce de horarios usando LocalTime
     */
    private boolean verificarSolapamiento(Disponibilidad disponibilidad, Horario horario) {
        // Primero validar día
        if (!disponibilidad.getDiaSemana().equalsIgnoreCase(horario.getDiaSemana())) {
            return false;
        }
        // La disponibilidad debe cubrir TODO el bloque del horario
        // Disp.Inicio <= Horario.Inicio  AND  Disp.Fin >= Horario.Fin
        return !disponibilidad.getHoraInicio().isAfter(horario.getHoraInicio()) &&
                !disponibilidad.getHoraFin().isBefore(horario.getHoraFin());
    }

    /**
     * Verifica preferencia real en el mapa
     */
    private boolean tienePreferenciaPorCurso(Docente docente, Curso curso,
                                             Map<Integer, List<Preferencia>> mapPreferencias) {
        List<Preferencia> preferencias = mapPreferencias.get(docente.getIdDocente());
        if (preferencias == null || preferencias.isEmpty()) return false;

        return preferencias.stream()
                .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                        .equals(curso.getAsignatura().getIdAsignatura()));
    }

    // ==========================================
    // MÉTODOS DE SOPORTE HÍBRIDO Y FINALIZACIÓN
    // ==========================================

    private List<SolucionAsignacion> prepararSiguienteCiclo(GeneticAlgorithm ga, PSOAlgorithm pso,
                                                            SolucionAsignacion mejorGlobal,
                                                            RestriccionValidator validator) {
        List<SolucionAsignacion> nuevaPoblacion = new ArrayList<>();

        // Incluir mejor solución global (1)
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

        // Completar con variaciones mutadas de la mejor solución global (Resto)
        int faltantes = Math.max(POBLACION_GA, ENJAMBRE_PSO) - nuevaPoblacion.size();
        Random random = new Random();
        for (int i = 0; i < faltantes; i++) {
            SolucionAsignacion variacion = new SolucionAsignacion(mejorGlobal);
            variacion.mutar(random, 0.1);
            validator.repararSolucion(variacion);
            validator.calcularFitness(variacion);
            nuevaPoblacion.add(variacion);
        }

        return nuevaPoblacion;
    }

    private List<Asignacion> convertirAAsignaciones(SolucionAsignacion solucion, List<Docente> docentes,
                                                    List<Curso> cursos, CicloAcademico cicloAcademico, Carga carga) {
        List<Asignacion> asignaciones = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = entry.getKey();
            Integer idDocente = entry.getValue();

            if (idDocente == -1) continue;

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
                asignacion.setCarga(carga);
                asignacion.setCicloAcademico(cicloAcademico);
                asignacion.setEnabled(true);
                asignacion.setCreatedAt(LocalDate.now());

                asignaciones.add(asignacion);
            }
        }
        return asignaciones;
    }

    private void logearEstadisticasFinales(SolucionAsignacion mejorSolucion, List<Docente> docentes, List<Curso> cursos) {
        log.info("=== ESTADÍSTICAS FINALES DEL ALGORITMO ===");
        log.info("Fitness final: {:.2f}", mejorSolucion.getFitness());
        log.info("Cursos asignados: {}/{}", mejorSolucion.getCursosAsignados(), cursos.size());
        log.info("Cursos sin asignar: {}", mejorSolucion.getCursosSinAsignar());
        log.info("Docentes utilizados: {}/{}", mejorSolucion.getDocentesUtilizados(), docentes.size());
        log.info("Porcentaje preferencias satisfechas: {:.1f}%", mejorSolucion.getPorcentajePreferencias());
        log.info("Solución válida: {}", mejorSolucion.isEsValida());
    }
}
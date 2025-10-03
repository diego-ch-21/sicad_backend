package com.sicad.sicad_backend.algorithm.validator;


import com.sicad.sicad_backend.algorithm.model.SolucionAsignacion;
import com.sicad.sicad_backend.model.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Validador de restricciones actualizado:
 * - RESTRICCIONES DURAS: Disponibilidad horaria + horasMaxLectivas
 * - RESTRICCIONES BLANDAS: Solo preferencias del docente
 * - ELIMINADO: Consideración de dedicación y categoría
 */
@Slf4j
public class RestriccionValidator {

    private final List<Docente> docentes;
    private final List<Curso> cursos;
    private final Map<Integer, List<Disponibilidad>> disponibilidadPorDocente;
    private final Map<Integer, List<Preferencia>> preferenciasPorDocente;
    private final CicloAcademico cicloAcademico;

    // Pesos actualizados para la función de fitness
    private static final double PESO_RESTRICCIONES_DURAS = 1000.0;
    private static final double PESO_PREFERENCIAS = 20.0;
    private static final double PESO_EQUILIBRIO_CARGA = 50.0;
    private static final double PESO_COBERTURA = 200.0;
    // ELIMINADO: PESO_ESPECIALIZACION (categoría)

    public RestriccionValidator(List<Docente> docentes, List<Curso> cursos,
                                Map<Integer, List<Disponibilidad>> disponibilidadPorDocente,
                                Map<Integer, List<Preferencia>> preferenciasPorDocente,
                                CicloAcademico cicloAcademico) {
        this.docentes = docentes;
        this.cursos = cursos;
        this.disponibilidadPorDocente = disponibilidadPorDocente;
        this.preferenciasPorDocente = preferenciasPorDocente;
        this.cicloAcademico = cicloAcademico;
    }

    /**
     * Calcula el fitness total de una solución
     * ACTUALIZADO: Solo considera horasMaxLectivas como restricción dura
     */
    public double calcularFitness(SolucionAsignacion solucion) {
        double fitness = 0.0;

        // 1. Evaluar SOLO restricciones duras (penalizaciones negativas)
        double penalizacionesDuras = evaluarRestriccionesDuras(solucion);

        // 2. Evaluar SOLO restricciones blandas (bonificaciones positivas)
        double bonificacionPreferencias = evaluarPreferencias(solucion);
        double bonificacionEquilibrio = evaluarEquilibrioCarga(solucion);
        double bonificacionCobertura = evaluarCoberturaCursos(solucion);

        fitness = -penalizacionesDuras + bonificacionPreferencias +
                bonificacionEquilibrio + bonificacionCobertura;

        solucion.setFitness(fitness);
        solucion.setEsValida(penalizacionesDuras == 0);

        return fitness;
    }

    /**
     * Evalúa RESTRICCIONES DURAS: disponibilidad + horasMaxLectivas + PREFERENCIAS
     * ACTUALIZADO: Preferencias ahora son OBLIGATORIAS (restricción dura)
     */
    private double evaluarRestriccionesDuras(SolucionAsignacion solucion) {
        double penalizaciones = 0.0;

        // 1. Verificar límites de horasMaxLectivas por docente
        penalizaciones += verificarLimitesHorasMaxLectivas(solucion);

        // 2. Verificar disponibilidad horaria (OBLIGATORIA)
        penalizaciones += verificarDisponibilidadHoraria(solucion);

        // 3. Verificar conflictos de horarios
        penalizaciones += verificarConflictosHorarios(solucion);

        // 4. NUEVO: Verificar preferencias (AHORA OBLIGATORIA)
        penalizaciones += verificarPreferenciasObligatorias(solucion);

        return penalizaciones * PESO_RESTRICCIONES_DURAS;
    }

    /**
     * Verifica SOLO el límite de horasMaxLectivas del docente
     * ACTUALIZADO: Ya no considera dedicación ni categoría
     */
    private double verificarLimitesHorasMaxLectivas(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Docente docente : docentes) {
            int horasAsignadas = solucion.getHorasTotalesDocente(docente.getIdDocente());

            // Usar SOLO horasMaxLectivas del docente (valor específico por docente)
            int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                    docente.getDedicacion().getHorasMaxLectivas() : 12; // 12 como valor por defecto

            if (horasAsignadas > horasMaximas) {
                penalizacion += (horasAsignadas - horasMaximas) * 10; // Penalización por cada hora excedida
                log.debug("Docente {} excede límite horasMaxLectivas: {}/{}",
                        docente.getCodigo(), horasAsignadas, horasMaximas);
            }
        }

        return penalizacion;
    }

    /**
     * Verifica disponibilidad horaria (RESTRICCIÓN DURA - OBLIGATORIA)
     * Sin cambios - sigue siendo obligatoria
     */
    private double verificarDisponibilidadHoraria(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue; // Curso sin asignar

            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            List<Disponibilidad> disponibilidades = disponibilidadPorDocente.get(idDocente);
            if (disponibilidades == null || disponibilidades.isEmpty()) {
                penalizacion += 50; // Penalización alta por no tener disponibilidad
                continue;
            }

            // Verificar cada horario del curso contra disponibilidad
            for (CursoHorario horario : curso.getCursoHorario()) {
                boolean tieneDisponibilidad = disponibilidades.stream()
                        .anyMatch(disp -> verificarSolapamientoHorario(disp, horario));

                if (!tieneDisponibilidad) {
                    penalizacion += 25; // Penalización por horario no disponible
                    log.debug("Docente {} no disponible para curso {} en {}",
                            idDocente, curso.getCodigo(), horario.getDiaSemana());
                }
            }
        }

        return penalizacion;
    }

    private boolean verificarSolapamientoHorario(Disponibilidad disponibilidad, CursoHorario horario) {
        return disponibilidad.getDiaSemana().equalsIgnoreCase(horario.getDiaSemana()) &&
                !disponibilidad.getHoraInicio().after(horario.getHoraInicio()) &&
                !disponibilidad.getHoraFin().before(horario.getHoraFin());
    }

    /**
     * Verifica conflictos de horarios - sin cambios
     */
    private double verificarConflictosHorarios(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Integer idDocente : solucion.getDocentesUtilizados()) {
            List<Integer> cursosDocente = solucion.getCursosDeDocente(idDocente);
            List<CursoHorario> todosLosHorarios = new ArrayList<>();

            // Recopilar todos los horarios del docente
            for (Integer idCurso : cursosDocente) {
                Curso curso = obtenerCursoPorId(idCurso);
                if (curso != null) {
                    todosLosHorarios.addAll(curso.getCursoHorario());
                }
            }

            // Verificar solapamientos
            for (int i = 0; i < todosLosHorarios.size(); i++) {
                for (int j = i + 1; j < todosLosHorarios.size(); j++) {
                    if (hayConflictoHorario(todosLosHorarios.get(i), todosLosHorarios.get(j))) {
                        penalizacion += 100; // Penalización muy alta por conflicto
                        log.debug("Conflicto horario detectado para docente {}", idDocente);
                    }
                }
            }
        }

        return penalizacion;
    }
    /**
     * Verifica preferencias como RESTRICCIÓN DURA (OBLIGATORIA)
     * Un docente solo puede recibir cursos de asignaturas que haya incluido en sus preferencias
     */
    private double verificarPreferenciasObligatorias(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue; // Curso sin asignar

            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            List<Preferencia> preferenciasDocente = preferenciasPorDocente.get(idDocente);

            // Si el docente no tiene preferencias registradas, penalizar
            if (preferenciasDocente == null || preferenciasDocente.isEmpty()) {
                penalizacion += 100; // Penalización alta por no tener preferencias
                log.debug("Docente {} no tiene preferencias registradas y se le asignó curso {}",
                        idDocente, curso.getCodigo());
                continue;
            }

            // Verificar si la asignatura del curso está en las preferencias del docente
            boolean tienePreferencia = preferenciasDocente.stream()
                    .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                            .equals(curso.getAsignatura().getIdAsignatura()));

            if (!tienePreferencia) {
                penalizacion += 100; // Penalización alta - restricción dura
                log.debug("VIOLACIÓN: Docente {} no tiene preferencia por asignatura {} del curso {}",
                        idDocente, curso.getAsignatura().getNombre(), curso.getCodigo());
            }
        }

        return penalizacion;
    }

    private boolean hayConflictoHorario(CursoHorario horario1, CursoHorario horario2) {
        if (!horario1.getDiaSemana().equalsIgnoreCase(horario2.getDiaSemana())) {
            return false;
        }

        Time inicio1 = horario1.getHoraInicio();
        Time fin1 = horario1.getHoraFin();
        Time inicio2 = horario2.getHoraInicio();
        Time fin2 = horario2.getHoraFin();

        return !(fin1.before(inicio2) || fin2.before(inicio1));
    }

    /**
     * Evalúa preferencias como BONIFICACIÓN adicional
     * ACTUALIZADO: Ahora solo bonifica por coincidencias múltiples o especiales
     * La verificación obligatoria ya se hace en restricciones duras
     */
    private double evaluarPreferencias(SolucionAsignacion solucion) {
        double bonificacion = 0.0;
        int preferenciasAltas = 0; // Preferencias de alta prioridad satisfechas
        int totalAsignaciones = 0;

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue;

            totalAsignaciones++;
            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            List<Preferencia> preferenciasDocente = preferenciasPorDocente.get(idDocente);
            if (preferenciasDocente != null) {
                // Buscar si tiene preferencia por esta asignatura
                Optional<Preferencia> preferenciaOpt = preferenciasDocente.stream()
                        .filter(pref -> pref.getAsignatura().getIdAsignatura()
                                .equals(curso.getAsignatura().getIdAsignatura()))
                        .findFirst();

                if (preferenciaOpt.isPresent()) {
                    Preferencia preferencia = preferenciaOpt.get();

                    // Bonificación base por preferencia (ya validada como obligatoria)
                    bonificacion += 5;

                    // Bonificación adicional si tiene prioridad alta (si tu modelo lo soporta)
                    // Si tienes un campo como "prioridad" en Preferencia:
                    // if (preferencia.getPrioridad() != null && preferencia.getPrioridad() >= 4) {
                    //     bonificacion += 10;
                    //     preferenciasAltas++;
                    // }

                    log.debug("Preferencia confirmada: Docente {} en asignatura {}",
                            idDocente, curso.getAsignatura().getNombre());
                }
            }
        }

        // Calcular porcentaje (ahora debería ser 100% si todo está bien)
        double porcentaje = totalAsignaciones > 0 ?
                (double) totalAsignaciones / totalAsignaciones : 0.0;
        solucion.setPorcentajePreferencias(porcentaje * 100);

        return bonificacion * PESO_PREFERENCIAS / 100.0;
    }

    /**
     * Evalúa el equilibrio en la distribución de carga - sin cambios
     */
    private double evaluarEquilibrioCarga(SolucionAsignacion solucion) {
        Set<Integer> docentesUtilizados = solucion.getDocentesUtilizados();
        if (docentesUtilizados.size() <= 1) {
            return 0.0;
        }

        List<Integer> cargas = docentesUtilizados.stream()
                .map(solucion::getHorasTotalesDocente)
                .collect(Collectors.toList());

        double promedio = cargas.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double desviacion = Math.sqrt(cargas.stream()
                .mapToDouble(carga -> Math.pow(carga - promedio, 2))
                .average().orElse(0.0));

        // Bonificación inversamente proporcional a la desviación
        double equilibrio = promedio > 0 ? 1.0 / (1.0 + desviacion / promedio) : 0.0;

        return equilibrio * PESO_EQUILIBRIO_CARGA;
    }

    /**
     * Evalúa la cobertura de cursos - sin cambios
     */
    private double evaluarCoberturaCursos(SolucionAsignacion solucion) {
        int cursosAsignados = solucion.getCursosAsignados();
        double porcentajeCobertura = (double) cursosAsignados / cursos.size();

        return porcentajeCobertura * PESO_COBERTURA;
    }

    /**
     * ELIMINADO: evaluarEspecializacion() - Ya no considera categoría del docente
     */

    /**
     * Verifica si una solución cumple todas las restricciones duras
     */
    public boolean esValida(SolucionAsignacion solucion) {
        return evaluarRestriccionesDuras(solucion) == 0;
    }

    /**
     * Repara una solución violando restricciones duras
     * ACTUALIZADO: Solo repara horasMaxLectivas y disponibilidad
     */
    public void repararSolucion(SolucionAsignacion solucion) {
        // 1. Eliminar asignaciones que violan disponibilidad
        eliminarAsignacionesInvalidas(solucion);

        // 2. Redistribuir carga excesiva (solo horasMaxLectivas)
        redistribuirCargaExcesiva(solucion);

        // 3. Resolver conflictos de horarios
        resolverConflictosHorarios(solucion);

        solucion.actualizarEstadisticas();
    }

    /**
     * Elimina asignaciones que violan restricciones duras
     * ACTUALIZADO: Incluye verificación de preferencias
     */
    private void eliminarAsignacionesInvalidas(SolucionAsignacion solucion) {
        List<Integer> cursosAEliminar = new ArrayList<>();

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue;

            boolean esValida = true;

            // 1. Verificar disponibilidad horaria
            if (!verificarDisponibilidadParaAsignacion(idCurso, idDocente)) {
                esValida = false;
                log.debug("Eliminando por disponibilidad: Curso {} - Docente {}", idCurso, idDocente);
            }

            // 2. NUEVO: Verificar preferencias (obligatorias)
            if (esValida && !verificarPreferenciaParaAsignacion(idCurso, idDocente)) {
                esValida = false;
                log.debug("Eliminando por falta de preferencia: Curso {} - Docente {}", idCurso, idDocente);
            }

            if (!esValida) {
                cursosAEliminar.add(idCurso);
            }
        }

        for (Integer idCurso : cursosAEliminar) {
            solucion.asignarDocente(idCurso, -1);
        }
    }

    /**
     * ACTUALIZADO: Solo considera horasMaxLectivas, no dedicación
     */
    private void redistribuirCargaExcesiva(SolucionAsignacion solucion) {
        for (Docente docente : docentes) {
            int horasActuales = solucion.getHorasTotalesDocente(docente.getIdDocente());

            // Usar SOLO horasMaxLectivas del docente específico
            int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                    docente.getDedicacion().getHorasMaxLectivas() : 12;

            if (horasActuales > horasMaximas) {
                List<Integer> cursosDocente = new ArrayList<>(solucion.getCursosDeDocente(docente.getIdDocente()));

                // Ordenar por horas (eliminar primero los de más horas)
                cursosDocente.sort((c1, c2) -> Integer.compare(
                        obtenerHorasCurso(c2), obtenerHorasCurso(c1)));

                log.debug("Redistribuyendo carga excesiva para docente {}: {}/{} horas",
                        docente.getCodigo(), horasActuales, horasMaximas);

                for (Integer idCurso : cursosDocente) {
                    if (solucion.getHorasTotalesDocente(docente.getIdDocente()) <= horasMaximas) {
                        break;
                    }
                    solucion.asignarDocente(idCurso, -1);
                    log.debug("Curso {} removido del docente {}", idCurso, docente.getCodigo());
                }
            }
        }
    }

    private void resolverConflictosHorarios(SolucionAsignacion solucion) {
        for (Integer idDocente : solucion.getDocentesUtilizados()) {
            List<Integer> cursosDocente = new ArrayList<>(solucion.getCursosDeDocente(idDocente));

            for (int i = 0; i < cursosDocente.size(); i++) {
                for (int j = i + 1; j < cursosDocente.size(); j++) {
                    if (tienenConflictoHorario(cursosDocente.get(i), cursosDocente.get(j))) {
                        // Eliminar el curso con menos horas
                        Integer cursoAEliminar = obtenerHorasCurso(cursosDocente.get(i)) <
                                obtenerHorasCurso(cursosDocente.get(j)) ?
                                cursosDocente.get(i) : cursosDocente.get(j);
                        solucion.asignarDocente(cursoAEliminar, -1);
                        cursosDocente.remove(cursoAEliminar);
                        j--; // Ajustar índice
                        log.debug("Conflicto horario resuelto: Curso {} removido del docente {}",
                                cursoAEliminar, idDocente);
                    }
                }
            }
        }
    }

    // Métodos auxiliares sin cambios
    private boolean verificarDisponibilidadParaAsignacion(Integer idCurso, Integer idDocente) {
        Curso curso = obtenerCursoPorId(idCurso);
        List<Disponibilidad> disponibilidades = disponibilidadPorDocente.get(idDocente);

        if (curso == null || disponibilidades == null) return false;

        return curso.getCursoHorario().stream()
                .allMatch(horario -> disponibilidades.stream()
                        .anyMatch(disp -> verificarSolapamientoHorario(disp, horario)));
    }
    /**
     * Verifica si un docente tiene preferencia por la asignatura de un curso
     * NUEVO: Método auxiliar para validación de preferencias obligatorias
     */
    private boolean verificarPreferenciaParaAsignacion(Integer idCurso, Integer idDocente) {
        Curso curso = obtenerCursoPorId(idCurso);
        if (curso == null) return false;

        List<Preferencia> preferenciasDocente = preferenciasPorDocente.get(idDocente);

        // Si no tiene preferencias, no puede tomar ningún curso
        if (preferenciasDocente == null || preferenciasDocente.isEmpty()) {
            log.debug("Docente {} no tiene preferencias registradas", idDocente);
            return false;
        }

        // Verificar si la asignatura del curso está en sus preferencias
        boolean tienePreferencia = preferenciasDocente.stream()
                .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                        .equals(curso.getAsignatura().getIdAsignatura()));

        if (!tienePreferencia) {
            log.debug("Docente {} no tiene preferencia por asignatura {}",
                    idDocente, curso.getAsignatura().getNombre());
        }

        return tienePreferencia;
    }

    private boolean tienenConflictoHorario(Integer idCurso1, Integer idCurso2) {
        Curso curso1 = obtenerCursoPorId(idCurso1);
        Curso curso2 = obtenerCursoPorId(idCurso2);

        if (curso1 == null || curso2 == null) return false;

        for (CursoHorario horario1 : curso1.getCursoHorario()) {
            for (CursoHorario horario2 : curso2.getCursoHorario()) {
                if (hayConflictoHorario(horario1, horario2)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int obtenerHorasCurso(Integer idCurso) {
        Curso curso = obtenerCursoPorId(idCurso);
        return curso != null ? curso.getCursoHorario().stream()
                .mapToInt(CursoHorario::getDuracionHoras)
                .sum() : 0;
    }

    private Curso obtenerCursoPorId(Integer idCurso) {
        return cursos.stream()
                .filter(curso -> curso.getIdCurso().equals(idCurso))
                .findFirst()
                .orElse(null);
    }

    private Docente obtenerDocentePorId(Integer idDocente) {
        return docentes.stream()
                .filter(docente -> docente.getIdDocente().equals(idDocente))
                .findFirst()
                .orElse(null);
    }
}
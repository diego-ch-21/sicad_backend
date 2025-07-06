package com.sicad.sicad_backend.algorithm.validator;

import com.sicad.sicad_backend.algorithm.model.SolucionAsignacion;
import com.sicad.sicad_backend.model.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Validador de restricciones duras y blandas para las asignaciones
 */
@Slf4j
public class RestriccionValidator {

    private final List<Docente> docentes;
    private final List<Curso> cursos;
    private final Map<Integer, List<Disponibilidad>> disponibilidadPorDocente;
    private final Map<Integer, List<Preferencia>> preferenciasPorDocente;
    private final CargaElectiva cargaElectiva;

    // Pesos para la función de fitness
    private static final double PESO_RESTRICCIONES_DURAS = 1000.0;
    private static final double PESO_PREFERENCIAS = 100.0;
    private static final double PESO_EQUILIBRIO_CARGA = 50.0;
    private static final double PESO_COBERTURA = 200.0;
    private static final double PESO_ESPECIALIZACION = 75.0;

    public RestriccionValidator(List<Docente> docentes, List<Curso> cursos,
                                Map<Integer, List<Disponibilidad>> disponibilidadPorDocente,
                                Map<Integer, List<Preferencia>> preferenciasPorDocente,
                                CargaElectiva cargaElectiva) {
        this.docentes = docentes;
        this.cursos = cursos;
        this.disponibilidadPorDocente = disponibilidadPorDocente;
        this.preferenciasPorDocente = preferenciasPorDocente;
        this.cargaElectiva = cargaElectiva;
    }

    /**
     * Calcula el fitness total de una solución
     */
    public double calcularFitness(SolucionAsignacion solucion) {
        double fitness = 0.0;

        // 1. Evaluar restricciones duras (penalizaciones negativas)
        double penalizacionesDuras = evaluarRestriccionesDuras(solucion);

        // 2. Evaluar restricciones blandas (bonificaciones positivas)
        double bonificacionPreferencias = evaluarPreferencias(solucion);
        double bonificacionEquilibrio = evaluarEquilibrioCarga(solucion);
        double bonificacionCobertura = evaluarCoberturaCursos(solucion);
        double bonificacionEspecializacion = evaluarEspecializacion(solucion);

        fitness = -penalizacionesDuras + bonificacionPreferencias + bonificacionEquilibrio +
                bonificacionCobertura + bonificacionEspecializacion;

        solucion.setFitness(fitness);
        solucion.setEsValida(penalizacionesDuras == 0);

        return fitness;
    }

    /**
     * Evalúa restricciones duras y retorna penalizaciones
     */
    private double evaluarRestriccionesDuras(SolucionAsignacion solucion) {
        double penalizaciones = 0.0;

        // 1. Verificar límites de horas lectivas por docente
        penalizaciones += verificarLimitesHorasLectivas(solucion);

        // 2. Verificar disponibilidad horaria
        penalizaciones += verificarDisponibilidadHoraria(solucion);

        // 3. Verificar conflictos de horarios (un docente no puede estar en dos lugares)
        penalizaciones += verificarConflictosHorarios(solucion);

        // 4. Verificar horas mínimas para docentes TC/DE
        penalizaciones += verificarHorasMinimas(solucion);

        return penalizaciones * PESO_RESTRICCIONES_DURAS;
    }

    private double verificarLimitesHorasLectivas(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Docente docente : docentes) {
            int horasAsignadas = solucion.getHorasTotalesDocente(docente.getIdDocente());
            int horasMaximas = docente.getHorasMaxLectivas() != null ? docente.getHorasMaxLectivas() : 12;

            if (horasAsignadas > horasMaximas) {
                penalizacion += (horasAsignadas - horasMaximas) * 10; // Penalización por cada hora excedida
                log.debug("Docente {} excede límite: {}/{}", docente.getCodigo(), horasAsignadas, horasMaximas);
            }
        }

        return penalizacion;
    }

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

            // Verificar cada horario del curso
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

    private double verificarHorasMinimas(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Docente docente : docentes) {
            // Solo para docentes TC (Tiempo Completo) y DE (Dedicación Exclusiva)
            String dedicacion = docente.getDedicacion().getNombre().toUpperCase();
            if (dedicacion.contains("COMPLETO") || dedicacion.contains("EXCLUSIVA")) {
                int horasAsignadas = solucion.getHorasTotalesDocente(docente.getIdDocente());
                if (horasAsignadas > 0 && horasAsignadas < 10) {
                    penalizacion += (10 - horasAsignadas) * 5; // Penalización por no cumplir mínimo
                    log.debug("Docente {} no cumple mínimo: {}/10", docente.getCodigo(), horasAsignadas);
                }
            }
        }

        return penalizacion;
    }

    /**
     * Evalúa qué tan bien se satisfacen las preferencias
     */
    private double evaluarPreferencias(SolucionAsignacion solucion) {
        double bonificacion = 0.0;
        int preferenciasSatisfechas = 0;
        int totalPreferencias = 0;

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue;

            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            List<Preferencia> preferenciasDocente = preferenciasPorDocente.get(idDocente);
            if (preferenciasDocente != null) {
                totalPreferencias++;

                boolean tienePreferencia = preferenciasDocente.stream()
                        .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                                .equals(curso.getAsignatura().getIdAsignatura()));

                if (tienePreferencia) {
                    preferenciasSatisfechas++;
                    bonificacion += 20; // Bonificación por preferencia satisfecha
                }
            }
        }

        // Calcular porcentaje de preferencias satisfechas
        double porcentaje = totalPreferencias > 0 ?
                (double) preferenciasSatisfechas / totalPreferencias : 0.0;
        solucion.setPorcentajePreferencias(porcentaje * 100);

        return bonificacion * PESO_PREFERENCIAS / 100.0;
    }

    /**
     * Evalúa el equilibrio en la distribución de carga
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
     * Evalúa la cobertura de cursos
     */
    private double evaluarCoberturaCursos(SolucionAsignacion solucion) {
        int cursosAsignados = solucion.getCursosAsignados();
        double porcentajeCobertura = (double) cursosAsignados / cursos.size();

        return porcentajeCobertura * PESO_COBERTURA;
    }

    /**
     * Evalúa la especialización (categoría del docente)
     */
    private double evaluarEspecializacion(SolucionAsignacion solucion) {
        double bonificacion = 0.0;

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idDocente = asignacion.getValue();
            if (idDocente == -1) continue;

            Docente docente = obtenerDocentePorId(idDocente);
            if (docente != null) {
                String categoria = docente.getCategoria().getNombre().toUpperCase();

                // Bonificación según categoría
                if (categoria.contains("PRINCIPAL")) {
                    bonificacion += 3;
                } else if (categoria.contains("ASOCIADO")) {
                    bonificacion += 2;
                } else if (categoria.contains("AUXILIAR")) {
                    bonificacion += 1;
                }
            }
        }

        return bonificacion * PESO_ESPECIALIZACION / 10.0;
    }

    /**
     * Verifica si una solución cumple todas las restricciones duras
     */
    public boolean esValida(SolucionAsignacion solucion) {
        return evaluarRestriccionesDuras(solucion) == 0;
    }

    /**
     * Repara una solución violando restricciones duras
     */
    public void repararSolucion(SolucionAsignacion solucion) {
        // 1. Eliminar asignaciones que violan disponibilidad
        eliminarAsignacionesInvalidas(solucion);

        // 2. Redistribuir carga excesiva
        redistribuirCargaExcesiva(solucion);

        // 3. Resolver conflictos de horarios
        resolverConflictosHorarios(solucion);

        solucion.actualizarEstadisticas();
    }

    private void eliminarAsignacionesInvalidas(SolucionAsignacion solucion) {
        List<Integer> cursosAEliminar = new ArrayList<>();

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue;

            if (!verificarDisponibilidadParaAsignacion(idCurso, idDocente)) {
                cursosAEliminar.add(idCurso);
            }
        }

        for (Integer idCurso : cursosAEliminar) {
            solucion.asignarDocente(idCurso, -1);
        }
    }

    private void redistribuirCargaExcesiva(SolucionAsignacion solucion) {
        for (Docente docente : docentes) {
            int horasActuales = solucion.getHorasTotalesDocente(docente.getIdDocente());
            int horasMaximas = docente.getHorasMaxLectivas() != null ? docente.getHorasMaxLectivas() : 12;

            if (horasActuales > horasMaximas) {
                List<Integer> cursosDocente = new ArrayList<>(solucion.getCursosDeDocente(docente.getIdDocente()));

                // Ordenar por horas (eliminar primero los de más horas)
                cursosDocente.sort((c1, c2) -> Integer.compare(
                        obtenerHorasCurso(c2), obtenerHorasCurso(c1)));

                for (Integer idCurso : cursosDocente) {
                    if (solucion.getHorasTotalesDocente(docente.getIdDocente()) <= horasMaximas) {
                        break;
                    }
                    solucion.asignarDocente(idCurso, -1);
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
                    }
                }
            }
        }
    }

    private boolean verificarDisponibilidadParaAsignacion(Integer idCurso, Integer idDocente) {
        Curso curso = obtenerCursoPorId(idCurso);
        List<Disponibilidad> disponibilidades = disponibilidadPorDocente.get(idDocente);

        if (curso == null || disponibilidades == null) return false;

        return curso.getCursoHorario().stream()
                .allMatch(horario -> disponibilidades.stream()
                        .anyMatch(disp -> verificarSolapamientoHorario(disp, horario)));
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
package com.sicad.sicad_backend.algorithm.validator;


import com.sicad.sicad_backend.algorithm.model.SolucionAsignacion;
import com.sicad.sicad_backend.model.*;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class RestriccionValidator {

    private final List<Docente> docentes;
    private final List<Curso> cursos;
    private final Map<Integer, List<Disponibilidad>> disponibilidadPorDocente;
    private final Map<Integer, List<Preferencia>> preferenciasPorDocente;
    private final CicloAcademico cicloAcademico;
    private static final double PESO_RESTRICCIONES_DURAS = 1000.0;
    private static final double PESO_PREFERENCIAS = 80.0;
    private static final double PESO_AFINIDAD_ESCUELA = 15.0;
    private static final double PESO_EQUILIBRIO_CARGA = 50.0;
    private static final double PESO_COBERTURA = 200.0;


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

    public double calcularFitness(SolucionAsignacion solucion) {
        double fitness = 0.0;

        double penalizacionesDuras = evaluarRestriccionesDuras(solucion);

        double bonificacionPreferencias = evaluarPreferencias(solucion);      // Solo Asignatura
        double bonificacionEscuela = evaluarAfinidadEscuela(solucion);        // NUEVO: Solo Escuela
        double bonificacionEquilibrio = evaluarEquilibrioCarga(solucion);
        double bonificacionCobertura = evaluarCoberturaCursos(solucion);

        fitness = -penalizacionesDuras
                + bonificacionPreferencias
                + bonificacionEscuela       // <--- AGREGADO
                + bonificacionEquilibrio
                + bonificacionCobertura;

        solucion.setFitness(fitness);
        solucion.setEsValida(penalizacionesDuras == 0);

        return fitness;
    }

    private double evaluarAfinidadEscuela(SolucionAsignacion solucion) {
        double puntosBonificacion = 0.0;

        for (Map.Entry<Integer, Integer> entry : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = entry.getKey();
            Integer idDocente = entry.getValue();

            if (idDocente == -1) continue;

            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            if (tienePreferenciaEscuela(idDocente, curso)) {
                puntosBonificacion += 10.0;
            }
        }

        return puntosBonificacion * PESO_AFINIDAD_ESCUELA;
    }

    private boolean tienePreferenciaEscuela(Integer idDocente, Curso curso) {
        List<Preferencia> prefs = preferenciasPorDocente.get(idDocente);
        if (prefs == null || prefs.isEmpty()) return false;

        return prefs.stream().anyMatch(pref ->
                pref.getAsignatura().getIdAsignatura().equals(curso.getAsignatura().getIdAsignatura())
                        &&
                        pref.getEscuela().getIdEscuela().equals(curso.getEscuela().getIdEscuela())
        );
    }

    private double evaluarRestriccionesDuras(SolucionAsignacion solucion) {
        double penalizaciones = 0.0;

        penalizaciones += verificarLimitesHorasMaxLectivas(solucion);

        penalizaciones += verificarDisponibilidadHoraria(solucion);

        penalizaciones += verificarConflictosHorarios(solucion);

        penalizaciones += verificarPreferenciasObligatorias(solucion);

        return penalizaciones * PESO_RESTRICCIONES_DURAS;
    }

    private double verificarLimitesHorasMaxLectivas(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Docente docente : docentes) {
            int horasAsignadas = solucion.getHorasTotalesDocente(docente.getIdDocente());

            int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                    docente.getDedicacion().getHorasMaxLectivas() : 20;

            if (horasAsignadas > horasMaximas) {
                penalizacion += (horasAsignadas - horasMaximas) * 10;
                log.debug("Docente {} excede límite horasMaxLectivas: {}/{}",
                        docente.getCodigo(), horasAsignadas, horasMaximas);
            }
        }

        return penalizacion;
    }

    private double verificarDisponibilidadHoraria(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue;

            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            List<Disponibilidad> disponibilidades = disponibilidadPorDocente.get(idDocente);
            if (disponibilidades == null || disponibilidades.isEmpty()) {
                penalizacion += 50;
                continue;
            }

            for (Horario horario : curso.getHorarios()) {
                boolean tieneDisponibilidad = disponibilidades.stream()
                        .anyMatch(disp -> verificarSolapamientoHorario(disp, horario));

                if (!tieneDisponibilidad) {
                    penalizacion += 25;
                    log.debug("Docente {} no disponible para curso {} en {}",
                            idDocente, "", horario.getDiaSemana());
                }
            }
        }

        return penalizacion;
    }

    private boolean verificarSolapamientoHorario(Disponibilidad disponibilidad, Horario horario) {
        return disponibilidad.getDiaSemana().equalsIgnoreCase(horario.getDiaSemana()) &&
                !disponibilidad.getHoraInicio().isAfter(horario.getHoraInicio()) && // disponibilidad.inicio <= horario.inicio
                !disponibilidad.getHoraFin().isBefore(horario.getHoraFin());      // disponibilidad.fin >= horario.fin
    }

    private double verificarConflictosHorarios(SolucionAsignacion solucion) {
        double penalizacion = 0.0;

        for (Integer idDocente : solucion.getDocentesUtilizados()) {
            List<Integer> cursosDocente = solucion.getCursosDeDocente(idDocente);
            List<Horario> todosLosHorarios = new ArrayList<>();

            for (Integer idCurso : cursosDocente) {
                Curso curso = obtenerCursoPorId(idCurso);
                if (curso != null) {
                    todosLosHorarios.addAll(curso.getHorarios());
                }
            }

            for (int i = 0; i < todosLosHorarios.size(); i++) {
                for (int j = i + 1; j < todosLosHorarios.size(); j++) {
                    if (hayConflictoHorario(todosLosHorarios.get(i), todosLosHorarios.get(j))) {
                        penalizacion += 100;
                        log.debug("Conflicto horario detectado para docente {}", idDocente);
                    }
                }
            }
        }

        return penalizacion;
    }

    private double verificarPreferenciasObligatorias(SolucionAsignacion solucion) {
        double penalizacion = 0.0;
        /*
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
                        idDocente, "");
                continue;
            }

            // Verificar si la asignatura del curso está en las preferencias del docente
            boolean tienePreferencia = preferenciasDocente.stream()
                    .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                            .equals(curso.getAsignatura().getIdAsignatura()));

            if (!tienePreferencia) {
                penalizacion += 100; // Penalización alta - restricción dura
                log.debug("VIOLACIÓN: Docente {} no tiene preferencia por asignatura {} del curso {}",
                        idDocente, curso.getAsignatura().getNombre(),"");
            }
        }

         */

        //return penalizacion;
        return penalizacion;
    }

    private boolean hayConflictoHorario(Horario horario1, Horario horario2) {
        if (!horario1.getDiaSemana().equalsIgnoreCase(horario2.getDiaSemana())) {
            return false;
        }

        LocalTime inicio1 = horario1.getHoraInicio();
        LocalTime fin1 = horario1.getHoraFin();
        LocalTime inicio2 = horario2.getHoraInicio();
        LocalTime fin2 = horario2.getHoraFin();

        return !(fin1.isBefore(inicio2) || fin1.equals(inicio2) || fin2.isBefore(inicio1) || fin2.equals(inicio1));
    }

    private double evaluarPreferencias(SolucionAsignacion solucion) {
        double puntosBonificacion = 0.0;
        int coincidencias = 0;
        int totalAsignaciones = 0;

        for (Map.Entry<Integer, Integer> entry : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = entry.getKey();
            Integer idDocente = entry.getValue();

            if (idDocente == -1) continue;

            Curso curso = obtenerCursoPorId(idCurso);
            if (curso == null) continue;

            totalAsignaciones++;

            if (tienePreferencia(idDocente, curso)) {
                coincidencias++;
                puntosBonificacion += 10.0;
            }
        }

        double porcentaje = (totalAsignaciones > 0)
                ? ((double) coincidencias / totalAsignaciones) * 100.0
                : 0.0;

        solucion.setPorcentajePreferencias(porcentaje);

        return puntosBonificacion * PESO_PREFERENCIAS;
    }
    private boolean tienePreferencia(Integer idDocente, Curso curso) {
        List<Preferencia> prefs = preferenciasPorDocente.get(idDocente);
        if (prefs == null || prefs.isEmpty()) return false;

        return prefs.stream()
                .anyMatch(pref -> pref.getAsignatura().getIdAsignatura()
                        .equals(curso.getAsignatura().getIdAsignatura()));
    }

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

        double equilibrio = promedio > 0 ? 1.0 / (1.0 + desviacion / promedio) : 0.0;

        return equilibrio * PESO_EQUILIBRIO_CARGA;
    }

    private double evaluarCoberturaCursos(SolucionAsignacion solucion) {
        int cursosAsignados = solucion.getCursosAsignados();
        double porcentajeCobertura = (double) cursosAsignados / cursos.size();

        return porcentajeCobertura * PESO_COBERTURA;
    }

    public boolean esValida(SolucionAsignacion solucion) {
        return evaluarRestriccionesDuras(solucion) == 0;
    }

    public void repararSolucion(SolucionAsignacion solucion) {
        eliminarAsignacionesInvalidas(solucion);

        redistribuirCargaExcesiva(solucion);

        resolverConflictosHorarios(solucion);

        solucion.actualizarEstadisticas();
    }

    private void eliminarAsignacionesInvalidas(SolucionAsignacion solucion) {
        List<Integer> cursosAEliminar = new ArrayList<>();

        for (Map.Entry<Integer, Integer> asignacion : solucion.getAsignaciones().entrySet()) {
            Integer idCurso = asignacion.getKey();
            Integer idDocente = asignacion.getValue();

            if (idDocente == -1) continue;

            boolean esValida = true;

            if (!verificarDisponibilidadParaAsignacion(idCurso, idDocente)) {
                esValida = false;
                log.debug("Eliminando por disponibilidad: Curso {} - Docente {}", idCurso, idDocente);
            }

            // 2. NUEVO: Verificar preferencias (obligatorias)
            /*
            if (esValida && !verificarPreferenciaParaAsignacion(idCurso, idDocente)) {
                esValida = false;
                log.debug("Eliminando por falta de preferencia: Curso {} - Docente {}", idCurso, idDocente);
            }

             */

            if (!esValida) {
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

            int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                    docente.getDedicacion().getHorasMaxLectivas() : 12;

            if (horasActuales > horasMaximas) {
                List<Integer> cursosDocente = new ArrayList<>(solucion.getCursosDeDocente(docente.getIdDocente()));

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
                        Integer cursoAEliminar = obtenerHorasCurso(cursosDocente.get(i)) <
                                obtenerHorasCurso(cursosDocente.get(j)) ?
                                cursosDocente.get(i) : cursosDocente.get(j);
                        solucion.asignarDocente(cursoAEliminar, -1);
                        cursosDocente.remove(cursoAEliminar);
                        j--;
                        log.debug("Conflicto horario resuelto: Curso {} removido del docente {}",
                                cursoAEliminar, idDocente);
                    }
                }
            }
        }
    }

    private boolean verificarDisponibilidadParaAsignacion(Integer idCurso, Integer idDocente) {
        Curso curso = obtenerCursoPorId(idCurso);
        List<Disponibilidad> disponibilidades = disponibilidadPorDocente.get(idDocente);

        if (curso == null || disponibilidades == null) return false;

        return curso.getHorarios().stream()
                .allMatch(horario -> disponibilidades.stream()
                        .anyMatch(disp -> verificarSolapamientoHorario(disp, horario)));
    }

    private boolean tienenConflictoHorario(Integer idCurso1, Integer idCurso2) {
        Curso curso1 = obtenerCursoPorId(idCurso1);
        Curso curso2 = obtenerCursoPorId(idCurso2);

        if (curso1 == null || curso2 == null) return false;

        for (Horario horario1 : curso1.getHorarios()) {
            for (Horario horario2 : curso2.getHorarios()) {
                if (hayConflictoHorario(horario1, horario2)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int obtenerHorasCurso(Integer idCurso) {
        Curso curso = obtenerCursoPorId(idCurso);
        return curso != null ? curso.getHorarios().stream()
                .mapToInt(Horario::getDuracionHoras)
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
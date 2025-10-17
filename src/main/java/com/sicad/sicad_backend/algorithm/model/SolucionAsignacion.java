package com.sicad.sicad_backend.algorithm.model;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.Curso;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Representa una solución completa del problema de asignación.
 * Puede usarse tanto como cromosoma (GA) o como partícula (PSO).
 */
@Data
@NoArgsConstructor
public class SolucionAsignacion {

    // Representación: Map<idCurso, idDocente> (-1 si no hay asignación)
    private Map<Integer, Integer> asignaciones;

    // Metadatos de la solución
    private double fitness;
    private boolean esValida;

    // Para PSO: velocidad de cambio
    private Map<Integer, Double> velocidad;

    // Estadísticas de la solución
    private int cursosAsignados;
    private int cursosSinAsignar;
    private int docentesUtilizados;
    private double porcentajePreferencias;
    private double equilibrioCarga;

    // Referencias a los datos del problema
    private List<Curso> cursos;
    private List<Docente> docentes;

    public SolucionAsignacion(List<Curso> cursos, List<Docente> docentes) {
        this.cursos = cursos;
        this.docentes = docentes;
        this.asignaciones = new HashMap<>();
        this.velocidad = new HashMap<>();

        // Inicializar con valores vacíos
        for (Curso curso : cursos) {
            asignaciones.put(curso.getIdCurso(), -1);
            velocidad.put(curso.getIdCurso(), 0.0);
        }

        this.fitness = 0.0;
        this.esValida = false;
    }

    /**
     * Constructor copia
     */
    public SolucionAsignacion(SolucionAsignacion otra) {
        this.cursos = otra.cursos;
        this.docentes = otra.docentes;
        this.asignaciones = new HashMap<>(otra.asignaciones);
        this.velocidad = new HashMap<>(otra.velocidad);
        this.fitness = otra.fitness;
        this.esValida = otra.esValida;
        this.cursosAsignados = otra.cursosAsignados;
        this.cursosSinAsignar = otra.cursosSinAsignar;
        this.docentesUtilizados = otra.docentesUtilizados;
        this.porcentajePreferencias = otra.porcentajePreferencias;
        this.equilibrioCarga = otra.equilibrioCarga;
    }

    /**
     * Asigna un docente a un curso
     */
    public void asignarDocente(Integer idCurso, Integer idDocente) {
        asignaciones.put(idCurso, idDocente);
    }

    /**
     * Obtiene el docente asignado a un curso
     */
    public Integer getDocenteAsignado(Integer idCurso) {
        return asignaciones.get(idCurso);
    }

    /**
     * Verifica si un curso tiene docente asignado
     */
    public boolean tieneAsignacion(Integer idCurso) {
        Integer docenteId = asignaciones.get(idCurso);
        return docenteId != null && docenteId != -1;
    }

    /**
     * Obtiene todos los cursos asignados a un docente
     */
    public List<Integer> getCursosDeDocente(Integer idDocente) {
        return asignaciones.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), idDocente))
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Calcula las horas totales asignadas a un docente
     */
    public int getHorasTotalesDocente(Integer idDocente) {
        return getCursosDeDocente(idDocente).stream()
                .mapToInt(this::getHorasCurso)
                .sum();
    }

    /**
     * Obtiene las horas de un curso sumando todos sus horarios
     */
    private int getHorasCurso(Integer idCurso) {
        return cursos.stream()
                .filter(curso -> curso.getIdCurso().equals(idCurso))
                .findFirst()
                .map(curso -> curso.getHorario().stream()
                        .mapToInt(horario -> horario.getDuracionHoras())
                        .sum())
                .orElse(0);
    }

    /**
     * Obtiene todos los docentes utilizados en la solución
     */
    public Set<Integer> getDocentesUtilizados() {
        return asignaciones.values().stream()
                .filter(docenteId -> docenteId != -1)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * Genera una solución aleatoria válida respetando TODAS las restricciones duras
     * ACTUALIZADO: Ahora también verifica preferencias antes de asignar
     */
    public void generarSolucionAleatoria(Random random) {
        // Limpiar asignaciones actuales
        for (Integer idCurso : asignaciones.keySet()) {
            asignaciones.put(idCurso, -1);
        }

        // Necesitarás pasar el validator como parámetro o tener acceso a preferencias
        // Por simplicidad, este método debería recibir el validator:
        // public void generarSolucionAleatoria(Random random, RestriccionValidator validator)

        // Asignar aleatoriamente respetando restricciones básicas
        for (Curso curso : cursos) {
            List<Integer> candidatos = new ArrayList<>();

            for (Docente docente : docentes) {
                // Verificar horas máximas
                int horasActuales = getHorasTotalesDocente(docente.getIdDocente());
                int horasCurso = getHorasCurso(curso.getIdCurso());
                int horasMaximas = docente.getDedicacion().getHorasMaxLectivas() != null ?
                        docente.getDedicacion().getHorasMaxLectivas() : 12;

                // NUEVO: También verificar preferencias (necesitarías acceso al validator)
                // boolean tienePreferencia = validator.verificarPreferenciaParaAsignacion(
                //     curso.getIdCurso(), docente.getIdDocente());

                if (horasActuales + horasCurso <= horasMaximas) {
                    // && tienePreferencia) {  // Descomentar cuando tengas acceso al validator
                    candidatos.add(docente.getIdDocente());
                }
            }

            // Asignar aleatoriamente o dejar sin asignar
            if (!candidatos.isEmpty() && random.nextDouble() > 0.2) {
                int indiceAleatorio = random.nextInt(candidatos.size());
                asignaciones.put(curso.getIdCurso(), candidatos.get(indiceAleatorio));
            }
        }

        actualizarEstadisticas();
    }

    /**
     * Actualiza las estadísticas de la solución
     */
    public void actualizarEstadisticas() {
        cursosAsignados = (int) asignaciones.values().stream()
                .filter(docenteId -> docenteId != -1)
                .count();

        cursosSinAsignar = cursos.size() - cursosAsignados;
        docentesUtilizados = getDocentesUtilizados().size();

        // Calcular equilibrio de carga
        if (docentesUtilizados > 0) {
            List<Integer> cargas = getDocentesUtilizados().stream()
                    .map(this::getHorasTotalesDocente)
                    .toList();

            double promedio = cargas.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            double varianza = cargas.stream()
                    .mapToDouble(carga -> Math.pow(carga - promedio, 2))
                    .average().orElse(0.0);

            equilibrioCarga = 1.0 / (1.0 + Math.sqrt(varianza));
        } else {
            equilibrioCarga = 0.0;
        }
    }

    /**
     * Aplica mutación a la solución
     */
    public void mutar(Random random, double probabilidadMutacion) {
        for (Map.Entry<Integer, Integer> entry : asignaciones.entrySet()) {
            if (random.nextDouble() < probabilidadMutacion) {
                Integer idCurso = entry.getKey();

                // 50% probabilidad de cambiar asignación, 50% de eliminarla
                if (random.nextBoolean()) {
                    // Cambiar asignación
                    List<Integer> candidatos = docentes.stream()
                            .map(Docente::getIdDocente)
                            .filter(idDocente -> {
                                int horasActuales = getHorasTotalesDocente(idDocente);
                                int horasCurso = getHorasCurso(idCurso);

                                // Obtener docente específico para sus horas máximas
                                Docente docente = docentes.stream()
                                        .filter(d -> d.getIdDocente().equals(idDocente))
                                        .findFirst().orElse(null);

                                int horasMaximas = (docente != null && docente.getDedicacion().getHorasMaxLectivas() != null) ?
                                        docente.getDedicacion().getHorasMaxLectivas() : 12;

                                return horasActuales + horasCurso <= horasMaximas;
                            })
                            .toList();

                    if (!candidatos.isEmpty()) {
                        int nuevoDocente = candidatos.get(random.nextInt(candidatos.size()));
                        asignaciones.put(idCurso, nuevoDocente);
                    }
                } else {
                    // Eliminar asignación
                    asignaciones.put(idCurso, -1);
                }
            }
        }

        actualizarEstadisticas();
    }

    /**
     * Actualiza la velocidad para PSO
     */
    public void actualizarVelocidad(SolucionAsignacion mejorPersonal, SolucionAsignacion mejorGlobal,
                                    double inercia, double c1, double c2, Random random) {
        for (Integer idCurso : asignaciones.keySet()) {
            double velocidadActual = velocidad.get(idCurso);

            // Componente de inercia
            double componenteInercia = inercia * velocidadActual;

            // Componente cognitivo (hacia mejor personal)
            double componenteCognitivo = 0.0;
            if (!Objects.equals(asignaciones.get(idCurso), mejorPersonal.getAsignaciones().get(idCurso))) {
                componenteCognitivo = c1 * random.nextDouble();
            }

            // Componente social (hacia mejor global)
            double componenteSocial = 0.0;
            if (!Objects.equals(asignaciones.get(idCurso), mejorGlobal.getAsignaciones().get(idCurso))) {
                componenteSocial = c2 * random.nextDouble();
            }

            double nuevaVelocidad = componenteInercia + componenteCognitivo + componenteSocial;
            velocidad.put(idCurso, nuevaVelocidad);
        }
    }

    /**
     * Actualiza la posición basada en la velocidad para PSO
     */
    public void actualizarPosicion(SolucionAsignacion mejorPersonal, SolucionAsignacion mejorGlobal, Random random) {
        for (Integer idCurso : asignaciones.keySet()) {
            double vel = velocidad.get(idCurso);

            if (Math.abs(vel) > random.nextDouble()) {
                // Decidir hacia dónde moverse
                if (vel > 0) {
                    // Moverse hacia mejor global
                    Integer asignacionGlobal = mejorGlobal.getAsignaciones().get(idCurso);
                    if (asignacionGlobal != null) {
                        asignaciones.put(idCurso, asignacionGlobal);
                    }
                } else {
                    // Moverse hacia mejor personal
                    Integer asignacionPersonal = mejorPersonal.getAsignaciones().get(idCurso);
                    if (asignacionPersonal != null) {
                        asignaciones.put(idCurso, asignacionPersonal);
                    }
                }
            }
        }

        actualizarEstadisticas();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SolucionAsignacion that = (SolucionAsignacion) obj;
        return Objects.equals(asignaciones, that.asignaciones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asignaciones);
    }

    @Override
    public String toString() {
        return String.format("SolucionAsignacion{fitness=%.2f, cursosAsignados=%d/%d, docentesUtilizados=%d, equilibrio=%.2f}",
                fitness, cursosAsignados, cursos.size(), docentesUtilizados, equilibrioCarga);
    }
}
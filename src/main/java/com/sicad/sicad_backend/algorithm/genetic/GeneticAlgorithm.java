package com.sicad.sicad_backend.algorithm.genetic;

import com.sicad.sicad_backend.algorithm.model.SolucionAsignacion;
import com.sicad.sicad_backend.algorithm.validator.RestriccionValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del Algoritmo Genético para asignación de docentes
 */
@Slf4j
public class GeneticAlgorithm {

    // Parámetros del algoritmo
    private final int tamañoPoblacion;
    private final double probabilidadCruzamiento;
    private final double probabilidadMutacion;
    private final int numeroGeneraciones;
    private final double porcentajeElitismo;

    // Componentes del algoritmo
    private final RestriccionValidator validator;
    private final Random random;

    // Estado del algoritmo
    private List<SolucionAsignacion> poblacion;
    private SolucionAsignacion mejorSolucion;
    private List<Double> historialFitness;

    public GeneticAlgorithm(int tamañoPoblacion, double probabilidadCruzamiento,
                            double probabilidadMutacion, int numeroGeneraciones,
                            double porcentajeElitismo, RestriccionValidator validator) {
        this.tamañoPoblacion = tamañoPoblacion;
        this.probabilidadCruzamiento = probabilidadCruzamiento;
        this.probabilidadMutacion = probabilidadMutacion;
        this.numeroGeneraciones = numeroGeneraciones;
        this.porcentajeElitismo = porcentajeElitismo;
        this.validator = validator;
        this.random = new Random();
        this.historialFitness = new ArrayList<>();
    }

    /**
     * Ejecuta el algoritmo genético completo
     */
    public SolucionAsignacion ejecutar(List<SolucionAsignacion> poblacionInicial) {
        log.info("Iniciando Algoritmo Genético - Población: {}, Generaciones: {}",
                tamañoPoblacion, numeroGeneraciones);

        // Inicializar población
        inicializarPoblacion(poblacionInicial);

        // Evolucionar por el número de generaciones especificado
        for (int generacion = 0; generacion < numeroGeneraciones; generacion++) {
            evolucionarGeneracion();

            // Logging cada 10 generaciones
            if (generacion % 10 == 0) {
                double mejorFitness = poblacion.stream()
                        .mapToDouble(SolucionAsignacion::getFitness)
                        .max().orElse(0.0);
                log.debug("Generación {}: Mejor fitness = {:.2f}", generacion, mejorFitness);
            }
        }

        // Encontrar y retornar la mejor solución
        actualizarMejorSolucion();
        log.info("Algoritmo Genético completado. Mejor fitness: {:.2f}", mejorSolucion.getFitness());

        return new SolucionAsignacion(mejorSolucion);
    }

    /**
     * Inicializa la población con las soluciones proporcionadas
     */
    private void inicializarPoblacion(List<SolucionAsignacion> poblacionInicial) {
        poblacion = new ArrayList<>();

        // Agregar soluciones iniciales
        for (SolucionAsignacion solucion : poblacionInicial) {
            if (poblacion.size() >= tamañoPoblacion) break;

            SolucionAsignacion copia = new SolucionAsignacion(solucion);
            validator.calcularFitness(copia);
            poblacion.add(copia);
        }

        // Generar soluciones adicionales si es necesario
        while (poblacion.size() < tamañoPoblacion) {
            if (!poblacionInicial.isEmpty()) {
                SolucionAsignacion nueva = new SolucionAsignacion(poblacionInicial.get(0));
                nueva.generarSolucionAleatoria(random);
                validator.repararSolucion(nueva);
                validator.calcularFitness(nueva);
                poblacion.add(nueva);
            }
        }

        actualizarMejorSolucion();
    }

    /**
     * Evoluciona una generación completa
     */
    private void evolucionarGeneracion() {
        List<SolucionAsignacion> nuevaPoblacion = new ArrayList<>();

        // 1. Elitismo: mantener los mejores individuos
        int numeroElites = (int) (tamañoPoblacion * porcentajeElitismo);
        List<SolucionAsignacion> elites = seleccionarElites(numeroElites);
        nuevaPoblacion.addAll(elites);

        // 2. Generar nueva descendencia
        while (nuevaPoblacion.size() < tamañoPoblacion) {
            // Selección de padres
            SolucionAsignacion padre1 = seleccionPorTorneo();
            SolucionAsignacion padre2 = seleccionPorTorneo();

            // Cruzamiento
            List<SolucionAsignacion> hijos = cruzamiento(padre1, padre2);

            // Mutación
            for (SolucionAsignacion hijo : hijos) {
                if (random.nextDouble() < probabilidadMutacion) {
                    hijo.mutar(random, probabilidadMutacion);
                }

                // Reparar y evaluar
                validator.repararSolucion(hijo);
                validator.calcularFitness(hijo);

                if (nuevaPoblacion.size() < tamañoPoblacion) {
                    nuevaPoblacion.add(hijo);
                }
            }
        }

        poblacion = nuevaPoblacion;
        actualizarMejorSolucion();

        // Registrar fitness promedio
        double fitnessPromedio = poblacion.stream()
                .mapToDouble(SolucionAsignacion::getFitness)
                .average().orElse(0.0);
        historialFitness.add(fitnessPromedio);
    }

    /**
     * Selecciona los mejores individuos para elitismo
     */
    private List<SolucionAsignacion> seleccionarElites(int numero) {
        return poblacion.stream()
                .sorted((s1, s2) -> Double.compare(s2.getFitness(), s1.getFitness()))
                .limit(numero)
                .map(SolucionAsignacion::new)
                .collect(Collectors.toList());
    }

    /**
     * Selección por torneo
     */
    private SolucionAsignacion seleccionPorTorneo() {
        int tamañoTorneo = Math.max(2, tamañoPoblacion / 10);
        SolucionAsignacion mejor = null;

        for (int i = 0; i < tamañoTorneo; i++) {
            SolucionAsignacion candidato = poblacion.get(random.nextInt(poblacion.size()));
            if (mejor == null || candidato.getFitness() > mejor.getFitness()) {
                mejor = candidato;
            }
        }

        return new SolucionAsignacion(mejor);
    }

    /**
     * Operador de cruzamiento uniforme
     */
    private List<SolucionAsignacion> cruzamiento(SolucionAsignacion padre1, SolucionAsignacion padre2) {
        List<SolucionAsignacion> hijos = new ArrayList<>();

        if (random.nextDouble() > probabilidadCruzamiento) {
            // No hay cruzamiento, retornar copias de los padres
            hijos.add(new SolucionAsignacion(padre1));
            hijos.add(new SolucionAsignacion(padre2));
            return hijos;
        }

        // Crear dos hijos
        SolucionAsignacion hijo1 = new SolucionAsignacion(padre1);
        SolucionAsignacion hijo2 = new SolucionAsignacion(padre2);

        // Cruzamiento uniforme
        for (Integer idCurso : padre1.getAsignaciones().keySet()) {
            if (random.nextBoolean()) {
                // Intercambiar asignaciones
                Integer asignacion1 = padre1.getDocenteAsignado(idCurso);
                Integer asignacion2 = padre2.getDocenteAsignado(idCurso);

                hijo1.asignarDocente(idCurso, asignacion2);
                hijo2.asignarDocente(idCurso, asignacion1);
            }
        }

        hijo1.actualizarEstadisticas();
        hijo2.actualizarEstadisticas();

        hijos.add(hijo1);
        hijos.add(hijo2);

        return hijos;
    }

    /**
     * Operador de cruzamiento por punto único
     */
    private List<SolucionAsignacion> cruzamientoPuntoUnico(SolucionAsignacion padre1, SolucionAsignacion padre2) {
        List<SolucionAsignacion> hijos = new ArrayList<>();

        if (random.nextDouble() > probabilidadCruzamiento) {
            hijos.add(new SolucionAsignacion(padre1));
            hijos.add(new SolucionAsignacion(padre2));
            return hijos;
        }

        List<Integer> cursos = new ArrayList<>(padre1.getAsignaciones().keySet());
        int puntoCruce = random.nextInt(cursos.size());

        SolucionAsignacion hijo1 = new SolucionAsignacion(padre1);
        SolucionAsignacion hijo2 = new SolucionAsignacion(padre2);

        // Intercambiar después del punto de cruce
        for (int i = puntoCruce; i < cursos.size(); i++) {
            Integer idCurso = cursos.get(i);
            Integer asignacion1 = padre1.getDocenteAsignado(idCurso);
            Integer asignacion2 = padre2.getDocenteAsignado(idCurso);

            hijo1.asignarDocente(idCurso, asignacion2);
            hijo2.asignarDocente(idCurso, asignacion1);
        }

        hijo1.actualizarEstadisticas();
        hijo2.actualizarEstadisticas();

        hijos.add(hijo1);
        hijos.add(hijo2);

        return hijos;
    }

    /**
     * Operador de cruzamiento por orden (OX)
     */
    private List<SolucionAsignacion> cruzamientoPorOrden(SolucionAsignacion padre1, SolucionAsignacion padre2) {
        // Implementación específica para preservar asignaciones válidas
        List<SolucionAsignacion> hijos = new ArrayList<>();

        SolucionAsignacion hijo1 = new SolucionAsignacion(padre1);
        SolucionAsignacion hijo2 = new SolucionAsignacion(padre2);

        List<Integer> cursos = new ArrayList<>(padre1.getAsignaciones().keySet());
        Collections.shuffle(cursos, random);

        int inicio = random.nextInt(cursos.size());
        int fin = inicio + random.nextInt(cursos.size() - inicio);

        // Preservar segmento del padre 1 en hijo 1
        Set<Integer> docentesUsados = new HashSet<>();
        for (int i = inicio; i <= fin; i++) {
            Integer idCurso = cursos.get(i);
            Integer idDocente = padre1.getDocenteAsignado(idCurso);
            if (idDocente != -1) {
                docentesUsados.add(idDocente);
            }
        }

        // Llenar el resto con asignaciones del padre 2 que no creen conflictos
        for (int i = 0; i < cursos.size(); i++) {
            if (i >= inicio && i <= fin) continue;

            Integer idCurso = cursos.get(i);
            Integer docentePadre2 = padre2.getDocenteAsignado(idCurso);

            if (docentePadre2 == -1 || !docentesUsados.contains(docentePadre2)) {
                hijo1.asignarDocente(idCurso, docentePadre2);
                if (docentePadre2 != -1) {
                    docentesUsados.add(docentePadre2);
                }
            }
        }

        // Proceso similar para hijo 2
        docentesUsados.clear();
        for (int i = inicio; i <= fin; i++) {
            Integer idCurso = cursos.get(i);
            Integer idDocente = padre2.getDocenteAsignado(idCurso);
            if (idDocente != -1) {
                docentesUsados.add(idDocente);
            }
        }

        for (int i = 0; i < cursos.size(); i++) {
            if (i >= inicio && i <= fin) continue;

            Integer idCurso = cursos.get(i);
            Integer docentePadre1 = padre1.getDocenteAsignado(idCurso);

            if (docentePadre1 == -1 || !docentesUsados.contains(docentePadre1)) {
                hijo2.asignarDocente(idCurso, docentePadre1);
                if (docentePadre1 != -1) {
                    docentesUsados.add(docentePadre1);
                }
            }
        }

        hijo1.actualizarEstadisticas();
        hijo2.actualizarEstadisticas();

        hijos.add(hijo1);
        hijos.add(hijo2);

        return hijos;
    }

    /**
     * Actualiza la mejor solución encontrada hasta el momento
     */
    private void actualizarMejorSolucion() {
        SolucionAsignacion candidato = poblacion.stream()
                .max(Comparator.comparingDouble(SolucionAsignacion::getFitness))
                .orElse(null);

        if (candidato != null && (mejorSolucion == null ||
                candidato.getFitness() > mejorSolucion.getFitness())) {
            mejorSolucion = new SolucionAsignacion(candidato);
        }
    }

    /**
     * Introduce diversidad en la población si hay convergencia prematura
     */
    private void introducirDiversidad() {
        double fitnessPromedio = poblacion.stream()
                .mapToDouble(SolucionAsignacion::getFitness)
                .average().orElse(0.0);

        double desviacion = Math.sqrt(poblacion.stream()
                .mapToDouble(s -> Math.pow(s.getFitness() - fitnessPromedio, 2))
                .average().orElse(0.0));

        // Si la desviación es muy baja, introducir diversidad
        if (desviacion < 1.0) {
            int numeroAReemplazar = tamañoPoblacion / 4;

            poblacion.sort((s1, s2) -> Double.compare(s1.getFitness(), s2.getFitness()));

            for (int i = 0; i < numeroAReemplazar; i++) {
                SolucionAsignacion nueva = new SolucionAsignacion(mejorSolucion);
                nueva.generarSolucionAleatoria(random);
                validator.repararSolucion(nueva);
                validator.calcularFitness(nueva);
                poblacion.set(i, nueva);
            }

            log.debug("Diversidad introducida en la población");
        }
    }

    // Getters para análisis y debugging
    public SolucionAsignacion getMejorSolucion() {
        return mejorSolucion != null ? new SolucionAsignacion(mejorSolucion) : null;
    }

    public List<SolucionAsignacion> getPoblacionActual() {
        return poblacion.stream()
                .map(SolucionAsignacion::new)
                .collect(Collectors.toList());
    }

    public List<Double> getHistorialFitness() {
        return new ArrayList<>(historialFitness);
    }

    public double getFitnessPromedio() {
        return poblacion.stream()
                .mapToDouble(SolucionAsignacion::getFitness)
                .average().orElse(0.0);
    }

    public double getMejorFitness() {
        return mejorSolucion != null ? mejorSolucion.getFitness() : 0.0;
    }
}
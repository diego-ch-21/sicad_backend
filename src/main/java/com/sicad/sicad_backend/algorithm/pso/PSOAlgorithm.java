package com.sicad.sicad_backend.algorithm.pso;

import com.sicad.sicad_backend.algorithm.model.SolucionAsignacion;
import com.sicad.sicad_backend.algorithm.validator.RestriccionValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del Algoritmo de Optimización por Enjambre de Partículas (PSO)
 * para la asignación de docentes
 */
@Slf4j
public class PSOAlgorithm {

    // Parámetros del algoritmo PSO
    private final int tamañoEnjambre;
    private final int numeroIteraciones;
    private final double inerciaInicial;
    private final double inerciaFinal;
    private final double c1; // Componente cognitivo
    private final double c2; // Componente social
    private final double velocidadMaxima;

    // Componentes del algoritmo
    private final RestriccionValidator validator;
    private final Random random;

    // Estado del enjambre
    private List<Particula> enjambre;
    private SolucionAsignacion mejorGlobal;
    private List<Double> historialFitness;

    public PSOAlgorithm(int tamañoEnjambre, int numeroIteraciones,
                        double inerciaInicial, double inerciaFinal,
                        double c1, double c2, double velocidadMaxima,
                        RestriccionValidator validator) {
        this.tamañoEnjambre = tamañoEnjambre;
        this.numeroIteraciones = numeroIteraciones;
        this.inerciaInicial = inerciaInicial;
        this.inerciaFinal = inerciaFinal;
        this.c1 = c1;
        this.c2 = c2;
        this.velocidadMaxima = velocidadMaxima;
        this.validator = validator;
        this.random = new Random();
        this.historialFitness = new ArrayList<>();
    }

    /**
     * Ejecuta el algoritmo PSO completo
     */
    public SolucionAsignacion ejecutar(List<SolucionAsignacion> poblacionInicial) {
        log.info("Iniciando Algoritmo PSO - Enjambre: {}, Iteraciones: {}",
                tamañoEnjambre, numeroIteraciones);

        // Inicializar enjambre
        inicializarEnjambre(poblacionInicial);

        // Iterar por el número de iteraciones especificado
        for (int iteracion = 0; iteracion < numeroIteraciones; iteracion++) {
            actualizarEnjambre(iteracion);

            // Logging cada 10 iteraciones
            if (iteracion % 10 == 0) {
                double mejorFitness = mejorGlobal != null ? mejorGlobal.getFitness() : 0.0;
                log.debug("Iteración {}: Mejor fitness global = {:.2f}", iteracion, mejorFitness);
            }
        }

        log.info("Algoritmo PSO completado. Mejor fitness: {:.2f}",
                mejorGlobal != null ? mejorGlobal.getFitness() : 0.0);

        return mejorGlobal != null ? new SolucionAsignacion(mejorGlobal) : null;
    }

    /**
     * Inicializa el enjambre con las soluciones proporcionadas
     */
    private void inicializarEnjambre(List<SolucionAsignacion> poblacionInicial) {
        enjambre = new ArrayList<>();

        // Crear partículas basadas en la población inicial
        for (int i = 0; i < tamañoEnjambre; i++) {
            SolucionAsignacion posicion;

            if (i < poblacionInicial.size()) {
                posicion = new SolucionAsignacion(poblacionInicial.get(i));
            } else {
                // Generar posición aleatoria basada en una solución existente
                posicion = new SolucionAsignacion(poblacionInicial.get(0));
                posicion.generarSolucionAleatoria(random);
            }

            // Reparar y evaluar
            validator.repararSolucion(posicion);
            validator.calcularFitness(posicion);

            // Crear partícula
            Particula particula = new Particula(posicion);
            enjambre.add(particula);

            // Actualizar mejor global
            if (mejorGlobal == null || posicion.getFitness() > mejorGlobal.getFitness()) {
                mejorGlobal = new SolucionAsignacion(posicion);
            }
        }

        log.debug("Enjambre inicializado con {} partículas", enjambre.size());
    }

    /**
     * Actualiza todas las partículas del enjambre en una iteración
     */
    private void actualizarEnjambre(int iteracion) {
        // Calcular inercia decreciente
        double inercia = calcularInercia(iteracion);

        for (Particula particula : enjambre) {
            // Actualizar velocidad
            particula.actualizarVelocidad(mejorGlobal, inercia, c1, c2, velocidadMaxima, random);

            // Actualizar posición
            particula.actualizarPosicion(mejorGlobal, random);

            // Reparar solución si es necesario
            validator.repararSolucion(particula.getPosicionActual());

            // Evaluar nueva posición
            validator.calcularFitness(particula.getPosicionActual());

            // Actualizar mejor personal
            particula.actualizarMejorPersonal();

            // Actualizar mejor global
            if (particula.getMejorPersonal().getFitness() > mejorGlobal.getFitness()) {
                mejorGlobal = new SolucionAsignacion(particula.getMejorPersonal());
            }
        }

        // Registrar fitness promedio
        double fitnessPromedio = enjambre.stream()
                .mapToDouble(p -> p.getPosicionActual().getFitness())
                .average().orElse(0.0);
        historialFitness.add(fitnessPromedio);

        // Aplicar estrategias de diversidad si es necesario
        if (iteracion % 20 == 0) {
            verificarYAplicarDiversidad();
        }
    }

    /**
     * Calcula la inercia decreciente linealmente
     */
    private double calcularInercia(int iteracion) {
        return inerciaInicial - ((inerciaInicial - inerciaFinal) * iteracion) / numeroIteraciones;
    }

    /**
     * Verifica convergencia y aplica estrategias de diversidad
     */
    private void verificarYAplicarDiversidad() {
        double fitnessPromedio = enjambre.stream()
                .mapToDouble(p -> p.getPosicionActual().getFitness())
                .average().orElse(0.0);

        double desviacion = Math.sqrt(enjambre.stream()
                .mapToDouble(p -> Math.pow(p.getPosicionActual().getFitness() - fitnessPromedio, 2))
                .average().orElse(0.0));

        // Si hay poca diversidad, reinicializar algunas partículas
        if (desviacion < 5.0) {
            int numeroAReemplazar = tamañoEnjambre / 4;

            enjambre.sort((p1, p2) -> Double.compare(
                    p1.getPosicionActual().getFitness(),
                    p2.getPosicionActual().getFitness()));

            for (int i = 0; i < numeroAReemplazar; i++) {
                SolucionAsignacion nuevaPosicion = new SolucionAsignacion(mejorGlobal);
                nuevaPosicion.generarSolucionAleatoria(random);
                validator.repararSolucion(nuevaPosicion);
                validator.calcularFitness(nuevaPosicion);

                enjambre.get(i).reinicializar(nuevaPosicion);
            }

            log.debug("Diversidad aplicada a {} partículas", numeroAReemplazar);
        }
    }

    /**
     * Aplica operador de mutación PSO
     */
    private void aplicarMutacion() {
        double probabilidadMutacion = 0.1;

        for (Particula particula : enjambre) {
            if (random.nextDouble() < probabilidadMutacion) {
                particula.getPosicionActual().mutar(random, 0.05);
                validator.repararSolucion(particula.getPosicionActual());
                validator.calcularFitness(particula.getPosicionActual());
                particula.actualizarMejorPersonal();
            }
        }
    }

    // Getters para análisis y debugging
    public SolucionAsignacion getMejorGlobal() {
        return mejorGlobal != null ? new SolucionAsignacion(mejorGlobal) : null;
    }

    public List<SolucionAsignacion> getPosicionesActuales() {
        return enjambre.stream()
                .map(p -> new SolucionAsignacion(p.getPosicionActual()))
                .collect(Collectors.toList());
    }

    public List<Double> getHistorialFitness() {
        return new ArrayList<>(historialFitness);
    }

    public double getFitnessPromedio() {
        return enjambre.stream()
                .mapToDouble(p -> p.getPosicionActual().getFitness())
                .average().orElse(0.0);
    }

    public double getMejorFitness() {
        return mejorGlobal != null ? mejorGlobal.getFitness() : 0.0;
    }

    /**
     * Clase interna que representa una partícula del enjambre
     */
    private static class Particula {
        private SolucionAsignacion posicionActual;
        private SolucionAsignacion mejorPersonal;

        public Particula(SolucionAsignacion posicionInicial) {
            this.posicionActual = new SolucionAsignacion(posicionInicial);
            this.mejorPersonal = new SolucionAsignacion(posicionInicial);
        }

        public void actualizarVelocidad(SolucionAsignacion mejorGlobal, double inercia,
                                        double c1, double c2, double velocidadMaxima, Random random) {
            posicionActual.actualizarVelocidad(mejorPersonal, mejorGlobal, inercia, c1, c2, random);

            // Limitar velocidad máxima
            for (Map.Entry<Integer, Double> entry : posicionActual.getVelocidad().entrySet()) {
                double velocidad = entry.getValue();
                if (Math.abs(velocidad) > velocidadMaxima) {
                    entry.setValue(Math.signum(velocidad) * velocidadMaxima);
                }
            }
        }

        public void actualizarPosicion(SolucionAsignacion mejorGlobal, Random random) {
            posicionActual.actualizarPosicion(mejorPersonal, mejorGlobal, random);
        }

        public void actualizarMejorPersonal() {
            if (posicionActual.getFitness() > mejorPersonal.getFitness()) {
                mejorPersonal = new SolucionAsignacion(posicionActual);
            }
        }

        public void reinicializar(SolucionAsignacion nuevaPosicion) {
            this.posicionActual = new SolucionAsignacion(nuevaPosicion);
            // No actualizar mejor personal para mantener memoria
        }

        public SolucionAsignacion getPosicionActual() {
            return posicionActual;
        }

        public SolucionAsignacion getMejorPersonal() {
            return mejorPersonal;
        }
    }
}
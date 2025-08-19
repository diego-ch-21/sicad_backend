package com.sicad.sicad_backend.dto.algoritmo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AlgoritmoUpdateRequest(
        @NotNull(message = "La población es obligatoria")
        @Positive(message = "La población debe ser un número positivo")
        Integer poblacion,

        @NotNull(message = "La generación GA es obligatoria")
        @Positive(message = "La generación GA debe ser un número positivo")
        Integer generacionGa,

        @DecimalMin(value = "0.0", message = "La probabilidad de cruzamientos no puede ser negativa")
        @DecimalMax(value = "1.0", message = "La probabilidad de cruzamientos no puede ser mayor a 1")
        double probCruzamientos,

        @DecimalMin(value = "0.0", message = "La probabilidad de mutación no puede ser negativa")
        @DecimalMax(value = "1.0", message = "La probabilidad de mutación no puede ser mayor a 1")
        double probMutacion,

        @DecimalMin(value = "0.0", message = "El elitismo no puede ser negativo")
        @DecimalMax(value = "1.0", message = "El elitismo no puede ser mayor a 1")
        double elitismo,

        @Positive(message = "El enjambre PSO debe ser un número positivo")
        Integer enjambrePso,

        @Positive(message = "Las iteraciones PSO deben ser un número positivo")
        Integer iteracionesPso,

        @DecimalMin(value = "0.0", message = "La inercia inicial no puede ser negativa")
        double inerciaInicial,

        @DecimalMin(value = "0.0", message = "La inercia final no puede ser negativa")
        double inerciaFinal,

        @DecimalMin(value = "0.0", message = "cUno no puede ser negativo")
        double cUno,

        @DecimalMin(value = "0.0", message = "cDos no puede ser negativo")
        double cDos,

        @DecimalMin(value = "0.0", message = "La velocidad máxima no puede ser negativa")
        double velocidadMaxima,

        @Positive(message = "El ciclo de híbridos debe ser un número positivo")
        Integer cicloHibridos
) {}
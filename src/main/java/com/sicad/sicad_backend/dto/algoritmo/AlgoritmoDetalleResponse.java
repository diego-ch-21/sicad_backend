package com.sicad.sicad_backend.dto.algoritmo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AlgoritmoDetalleResponse(
        Integer idAlgoritmo,
        Integer poblacion,
        Integer generacionGa,
        double probCruzamientos,
        double probMutacion,
        double elitismo,
        Integer enjambrePso,
        Integer iteracionesPso,
        double inerciaInicial,
        double inerciaFinal,
        double cUno,
        double cDos,
        double velocidadMaxima,
        Integer cicloHibridos,
        LocalDateTime createdAt,
        boolean enabled
) {}
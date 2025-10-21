package com.sicad.sicad_backend.dto.algoritmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 👈 Necesario para ModelMapper
@AllArgsConstructor // 👈 Constructor con todos los campos
public class AlgoritmoUpdateRequest {

    @NotNull(message = "La población es obligatoria")
    @Positive(message = "La población debe ser un número positivo")
    private Integer poblacion;

    @NotNull(message = "La generación GA es obligatoria")
    @Positive(message = "La generación GA debe ser un número positivo")
    private Integer generacionGa;

    @DecimalMin(value = "0.0", message = "La probabilidad de cruzamientos no puede ser negativa")
    @DecimalMax(value = "1.0", message = "La probabilidad de cruzamientos no puede ser mayor a 1")
    private Double probCruzamientos;

    @DecimalMin(value = "0.0", message = "La probabilidad de mutación no puede ser negativa")
    @DecimalMax(value = "1.0", message = "La probabilidad de mutación no puede ser mayor a 1")
    private Double probMutacion;

    @DecimalMin(value = "0.0", message = "El elitismo no puede ser negativo")
    @DecimalMax(value = "1.0", message = "El elitismo no puede ser mayor a 1")
    private Double elitismo;

    @Positive(message = "El enjambre PSO debe ser un número positivo")
    private Integer enjambrePso;

    @Positive(message = "Las iteraciones PSO deben ser un número positivo")
    private Integer iteracionesPso;

    @DecimalMin(value = "0.0", message = "La inercia inicial no puede ser negativa")
    private Double inerciaInicial;

    @DecimalMin(value = "0.0", message = "La inercia final no puede ser negativa")
    private Double inerciaFinal;

    @DecimalMin(value = "0.0", message = "cUno no puede ser negativo")
    @JsonProperty("cUno")
    private Double cUno;

    @DecimalMin(value = "0.0", message = "cDos no puede ser negativo")
    @JsonProperty("cDos")
    private Double cDos;

    @DecimalMin(value = "0.0", message = "La velocidad máxima no puede ser negativa")
    private Double velocidadMaxima;

    @Positive(message = "El ciclo de híbridos debe ser un número positivo")
    private Integer cicloHibridos;
}

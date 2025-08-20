package com.sicad.sicad_backend.dto.algoritmo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // 👈 Necesario para ModelMapper
@AllArgsConstructor // 👈 Constructor con todos los campos
public class AlgoritmoDetalleResponse {

    private Integer idAlgoritmo;
    private boolean principal;
    private Integer poblacion;
    private Integer generacionGa;
    private double probCruzamientos;
    private double probMutacion;
    private double elitismo;
    private Integer enjambrePso;
    private Integer iteracionesPso;
    private double inerciaInicial;
    private double inerciaFinal;
    @JsonProperty("cUno")
    private double cUno;
    @JsonProperty("cdos")
    private double cDos;
    private double velocidadMaxima;
    private Integer cicloHibridos;
    private LocalDateTime createdAt;
    private boolean enabled;
}

package com.sicad.sicad_backend.dto.dedicacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DedicacionRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDedicacion;

    private String nombre;
    private Integer horasTotales;
    private Integer horasLectivasMinima;
    private Double porcentajeLectivoMinimo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabled=true;
}

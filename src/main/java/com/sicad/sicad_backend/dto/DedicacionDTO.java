package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DedicacionDTO {
    private String idDedicacion;
    private String nombre;
    private Integer horasTotales;
    private Integer horasLectivasMinima;
    private Double porcentajeLectivoMinimo;
    private Boolean enabled;
}

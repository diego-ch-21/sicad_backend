package com.sicad.sicad_backend.dto.disponibilidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadResumenResponse {
    private Integer idDisponibilidad;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Boolean enabled;
}

package com.sicad.sicad_backend.dto.disponibilidad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadUpdateRequest {
    private Integer idDocente;
    private Integer idCargaElectiva;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
}

package com.sicad.sicad_backend.dto.disponibilidad;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDisponibilidad;
    private Integer idDocente;
    private String diaSemana;
    private Time horaInicio;
    private Time horaFin;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}

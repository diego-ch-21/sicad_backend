package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadDTO {
    private Integer idDisponibilidad;
    private Integer idDocente;
    private String diaSemana;
    private Time horaInicio;
    private Time horaFin;
}

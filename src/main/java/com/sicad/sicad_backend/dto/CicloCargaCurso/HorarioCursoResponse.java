package com.sicad.sicad_backend.dto.CicloCargaCurso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioCursoResponse {
    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String tipoSesion;
    private String aula;
}
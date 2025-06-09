package com.sicad.sicad_backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioCursoDTO {
    private Integer idHorario;
    private Integer idCurso;
    private String grupo;
    private String tipoSesion;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String aula;
    private Integer duracionHoras;
}

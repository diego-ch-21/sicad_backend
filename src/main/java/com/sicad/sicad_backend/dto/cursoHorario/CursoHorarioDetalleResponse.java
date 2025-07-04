package com.sicad.sicad_backend.dto.cursoHorario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoHorarioDetalleResponse {
    private Integer idCursoHorario;
    private String tipoSesion;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Integer duracionHoras;
    private boolean enabled;
}

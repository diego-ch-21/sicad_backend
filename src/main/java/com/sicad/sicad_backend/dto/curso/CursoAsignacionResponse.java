package com.sicad.sicad_backend.dto.curso;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import com.sicad.sicad_backend.dto.Horario.HorarioDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoAsignacionResponse {
    private Integer idCurso;
    private AsignaturaResumenResponse asignatura;
    private List<String> planDeEstudios;
    private EscuelaResumenResponse escuela;
    private List<HorarioDetalleResponse> horario;
    private String grupo;
    private boolean enabled;
}

package com.sicad.sicad_backend.dto.curso;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
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
public class CursoDetalleResponse {
    private Integer idCurso;
    private String codigo;
    private String grupo;
    private AsignaturaResumenResponse asignatura;
    private List<String> planDeEstudios;
    private EscuelaResumenResponse escuela;
    private CicloAcademicoResumenResponse cicloAcademico;
    private List<HorarioDetalleResponse> horario;
    private boolean enabled;
}

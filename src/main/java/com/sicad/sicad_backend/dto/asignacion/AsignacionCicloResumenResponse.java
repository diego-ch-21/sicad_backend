package com.sicad.sicad_backend.dto.asignacion;

import com.sicad.sicad_backend.dto.curso.CursoAsignacionResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionCicloResumenResponse {
    private Integer idAsignacion;
    private CursoAsignacionResponse curso;
    private DocenteResumenResponse docente;
    private Boolean enabled;
}

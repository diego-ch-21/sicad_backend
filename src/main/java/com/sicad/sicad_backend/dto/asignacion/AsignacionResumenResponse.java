package com.sicad.sicad_backend.dto.asignacion;

 import com.sicad.sicad_backend.dto.curso.CursoAsignacionResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionResumenResponse {
    private Integer idAsignacion;
    private CursoAsignacionResponse curso;
    private Boolean enabled;
}

package com.sicad.sicad_backend.dto.asignacion;

import com.sicad.sicad_backend.dto.cargaElectiva.CargaElectivaResumenResponse;
import com.sicad.sicad_backend.dto.curso.CursoAsignacionResponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionResumenResponse {
    private CursoAsignacionResponse curso;
    private Boolean enabled;
}

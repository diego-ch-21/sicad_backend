package com.sicad.sicad_backend.dto.asignacion;

import com.sicad.sicad_backend.dto.cargaElectiva.CargaElectivaResumenResponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.curso.CursoResumenResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionDetalleResponse {
    private DocenteResumenResponse docente;
    private CursoDetalleResponse curso;
    private CargaElectivaResumenResponse cargaElectiva;
    private String tipoAsignacion;
    private String createdAt;
    private Boolean enabled;
}

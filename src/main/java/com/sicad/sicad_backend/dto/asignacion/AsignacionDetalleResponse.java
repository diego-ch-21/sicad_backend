package com.sicad.sicad_backend.dto.asignacion;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.dto.curso.CursoDetalleResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionDetalleResponse {
    private Integer idAsignacion;
    private DocenteResumenResponse docente;
    private CursoDetalleResponse curso;
    private CicloAcademicoResumenResponse cicloAcademico;
    private AlgoritmoResumenResponse algoritmo;
    private String tipoAsignacion;
    private String createdAt;
    private Boolean enabled;
}

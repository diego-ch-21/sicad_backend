package com.sicad.sicad_backend.dto.preMatricula;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreMatriculaDetalleResponse {
    private Integer idPreMatricula;
    private AsignaturaDetalleResponse asignatura;
    private CicloAcademicoResumenResponse cicloAcademico;
    private Integer cantidad;
    private boolean enabled;
}

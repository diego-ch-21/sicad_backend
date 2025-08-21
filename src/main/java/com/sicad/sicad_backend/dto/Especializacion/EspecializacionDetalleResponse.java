package com.sicad.sicad_backend.dto.Especializacion;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import com.sicad.sicad_backend.model.Asignatura;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecializacionDetalleResponse {
    private Integer idEspecializacion;
    private AsignaturaResumenResponse asignatura;
    private DocenteResumenResponse docente;
    private Boolean enabled;
}

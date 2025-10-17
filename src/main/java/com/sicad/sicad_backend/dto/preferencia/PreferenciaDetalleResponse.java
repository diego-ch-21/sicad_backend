package com.sicad.sicad_backend.dto.preferencia;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaDetalleResponse {
    private Integer idPreferencia;
    private DocenteResumenResponse docente;
    private AsignaturaResumenResponse asignatura;
    private CicloAcademicoResumenResponse cicloAcademico;
    private EscuelaResumenResponse escuela;
    private boolean enabled;
}

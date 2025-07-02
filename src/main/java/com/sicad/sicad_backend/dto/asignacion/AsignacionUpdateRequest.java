package com.sicad.sicad_backend.dto.asignacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionUpdateRequest {
    private Integer idDocente;
    private Integer idCurso;
    private Integer idCargaElectiva;
    private String tipoAsignacion;
}

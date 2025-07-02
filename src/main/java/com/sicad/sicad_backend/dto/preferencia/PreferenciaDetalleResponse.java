package com.sicad.sicad_backend.dto.preferencia;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import com.sicad.sicad_backend.dto.cargaElectiva.CargaElectivaResumenResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import com.sicad.sicad_backend.model.CargaElectiva;
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
    private CargaElectivaResumenResponse cargaElectiva;
    private boolean enabled;
}

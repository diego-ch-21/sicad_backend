package com.sicad.sicad_backend.dto.planDeEstudio;

import com.sicad.sicad_backend.dto.facultad.FacultadResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeEstudioDetalleResponse {
    private Integer idPlanDeEstudio;
    private Integer codigo;
    private String nombre;
    private Boolean enabled;
}

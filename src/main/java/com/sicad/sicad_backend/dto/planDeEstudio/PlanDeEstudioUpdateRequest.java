package com.sicad.sicad_backend.dto.planDeEstudio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeEstudioUpdateRequest {
    private Integer idFacultad;
    private String nombre;
}

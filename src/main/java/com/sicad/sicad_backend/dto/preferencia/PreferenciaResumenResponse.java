package com.sicad.sicad_backend.dto.preferencia;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaResumenResponse {
    private Integer idPreferencia;
    private AsignaturaResumenResponse asignatura;
    private boolean enabled;
}

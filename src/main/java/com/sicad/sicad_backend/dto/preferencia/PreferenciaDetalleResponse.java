package com.sicad.sicad_backend.dto.preferencia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaDetalleResponse {
    private Integer idPreferencia;
    private Integer idDocente;
    private Integer idCurso;
    private boolean enabled;
}

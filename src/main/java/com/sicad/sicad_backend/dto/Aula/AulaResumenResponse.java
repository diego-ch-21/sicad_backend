package com.sicad.sicad_backend.dto.Aula;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AulaResumenResponse {
    private Integer idAula;
    private String tipo;
    private boolean enabled;
}

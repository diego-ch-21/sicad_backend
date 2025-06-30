package com.sicad.sicad_backend.dto.escuela;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaResumenResponse {
    private Integer idEscuela;
    private String nombre;
}

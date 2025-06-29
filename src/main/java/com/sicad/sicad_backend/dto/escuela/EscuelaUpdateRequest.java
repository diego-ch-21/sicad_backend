package com.sicad.sicad_backend.dto.escuela;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaUpdateRequest {
    private String nombre;
    private Integer idFacultad;
}

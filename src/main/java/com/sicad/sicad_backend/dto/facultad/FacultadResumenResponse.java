package com.sicad.sicad_backend.dto.facultad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultadResumenResponse {
    private Integer idFacultad;
    private String nombre;
}

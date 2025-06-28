package com.sicad.sicad_backend.dto.facultad;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultadDetalleResponse {
    private Integer idFacultad;
    private String nombre;
    private boolean enabled;
}

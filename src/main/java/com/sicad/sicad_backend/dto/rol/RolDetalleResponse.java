package com.sicad.sicad_backend.dto.rol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDetalleResponse{
    private Integer idRol;
    private String nombre;
    private boolean enabled;
}
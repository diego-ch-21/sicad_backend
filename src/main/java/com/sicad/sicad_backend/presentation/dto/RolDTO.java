package com.sicad.sicad_backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDTO {
    private Integer idRol;
    private String nombre;
    private boolean enabled;
}

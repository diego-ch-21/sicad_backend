package com.sicad.sicad_backend.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResumenResponse {
    private String nombre;
    private String apellido;
}

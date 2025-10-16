package com.sicad.sicad_backend.dto.escuelaProfesional;

import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaProfesionalDetalleResponse {
    private Integer idDirector;
    private UsuarioDetalleResponse usuario;
    private boolean enabled;
}

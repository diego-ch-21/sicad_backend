package com.sicad.sicad_backend.dto.logistica;

import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticaDetalleResponse {
    private Integer idLogistica;
    private UsuarioDTO usuario;
    private String cargo;
    private boolean enabled;
}

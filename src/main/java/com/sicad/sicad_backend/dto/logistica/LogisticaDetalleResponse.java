package com.sicad.sicad_backend.dto.logistica;

import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticaDetalleResponse {
    private Integer idLogistica;
    private UsuarioDetalleResponse usuario;
    private boolean enabled;
}

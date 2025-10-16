package com.sicad.sicad_backend.dto.jefeDepartamento;

import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JefeDepartamentoDetalleResponse {
    private Integer idJefeDepartamento;
    private UsuarioDetalleResponse usuario;
    private String cargo;
    private boolean enabled;
}

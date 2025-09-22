package com.sicad.sicad_backend.dto.jefeDepartamento;

import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JefeDepartamentoDetalleResponse {
    private Integer idJefeDepartamento;
    private UsuarioDTO usuario;
    private String cargo;
    private boolean enabled;
}

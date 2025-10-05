package com.sicad.sicad_backend.dto.director;

import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorDetalleResponse {
    private Integer idDirector;
    private UsuarioDTO usuario;
    private boolean enabled;
    private EscuelaResumenResponse escuela;
}

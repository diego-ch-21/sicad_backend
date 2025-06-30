package com.sicad.sicad_backend.dto.director;

import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import com.sicad.sicad_backend.dto.facultad.FacultadResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorDetalleResponse {
    private Integer idDirector;
    private UsuarioDTO usuario;
    private String cargo;
    private boolean enabled;
    private FacultadResumenResponse facultad;
}

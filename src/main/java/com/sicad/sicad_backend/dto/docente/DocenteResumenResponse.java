package com.sicad.sicad_backend.dto.docente;

import com.sicad.sicad_backend.dto.usuario.UsuarioResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteResumenResponse {
    private Integer idDocente;
    private UsuarioResumenResponse usuario;
}

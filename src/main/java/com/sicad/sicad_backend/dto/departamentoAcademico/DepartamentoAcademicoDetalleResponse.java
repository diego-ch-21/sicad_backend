package com.sicad.sicad_backend.dto.departamentoAcademico;

import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoAcademicoDetalleResponse {
    private Integer idDirector;
    private UsuarioDetalleResponse usuario;
    private boolean enabled;
}

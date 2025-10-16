package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.preferencia.PreferenciaResumenResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocentePreferenciaResponse {
    private Integer idDocente;
    private UsuarioResumenResponse usuario;
    private List<PreferenciaResumenResponse> preferencias;
    private boolean enabled;
}

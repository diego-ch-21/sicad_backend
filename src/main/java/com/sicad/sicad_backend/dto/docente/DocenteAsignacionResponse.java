package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.asignacion.AsignacionResumenResponse;
import com.sicad.sicad_backend.dto.usuario.UsuarioResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocenteAsignacionResponse {
    private Integer idDocente;
    private String codigo;
    private UsuarioResumenResponse usuario;
    private List<AsignacionResumenResponse> asignaciones;
    private boolean enabled;
}

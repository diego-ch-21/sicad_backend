package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.usuario.UsuarioDTO;
import com.sicad.sicad_backend.dto.categoria.CategoriaResumenResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocenteDetalleResponse {
    private Integer idDocente;
    private UsuarioDTO usuario;
    private DedicacionResumenResponse dedicacion;
    private CategoriaResumenResponse categoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
    private Date createdAt;
    private boolean enabled;
}

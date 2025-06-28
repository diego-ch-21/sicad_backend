package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.categoria.CategoriaResumenResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteUsuarioResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDocente;
    private DedicacionResumenResponse dedicacion;
    private CategoriaResumenResponse categoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
    private Date createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
}

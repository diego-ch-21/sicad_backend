package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.UsuarioDTO;
import com.sicad.sicad_backend.dto.categoria.CategoriaRequestDTO;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocenteResponseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDocente;
    private UsuarioDTO usuario;
    private DedicacionRequestDTO dedicacion;
    private CategoriaRequestDTO categoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
    private Date createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
}

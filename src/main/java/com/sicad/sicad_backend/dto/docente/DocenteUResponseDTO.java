package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.categoria.CategoriaUDResponseDTO;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionUDResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteUResponseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDocente;
    private DedicacionUDResponseDTO dedicacion;
    private CategoriaUDResponseDTO categoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
    private Date createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
}

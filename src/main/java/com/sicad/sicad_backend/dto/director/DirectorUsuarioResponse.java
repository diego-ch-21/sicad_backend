package com.sicad.sicad_backend.dto.director;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorUsuarioResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDirector;
    private EscuelaResumenResponse escuela;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}

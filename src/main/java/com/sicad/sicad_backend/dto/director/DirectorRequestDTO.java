package com.sicad.sicad_backend.dto.director;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDirector;
    private Integer idUsuario;
    private String cargo;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}

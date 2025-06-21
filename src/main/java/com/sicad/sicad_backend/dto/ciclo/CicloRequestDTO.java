package com.sicad.sicad_backend.dto.ciclo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idCiclo;
    private String nombre;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}

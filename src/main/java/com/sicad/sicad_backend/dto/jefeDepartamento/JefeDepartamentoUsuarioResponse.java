package com.sicad.sicad_backend.dto.jefeDepartamento;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JefeDepartamentoUsuarioResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idJefeDepartamento;
    private String cargo;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}

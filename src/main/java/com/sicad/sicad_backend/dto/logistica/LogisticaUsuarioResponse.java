package com.sicad.sicad_backend.dto.logistica;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticaUsuarioResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idLogistica;
    private String cargo;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}

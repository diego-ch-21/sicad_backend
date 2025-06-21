package com.sicad.sicad_backend.dto.categoria;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idCategoria;
    private String nombre;
    private String descripcion;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
}

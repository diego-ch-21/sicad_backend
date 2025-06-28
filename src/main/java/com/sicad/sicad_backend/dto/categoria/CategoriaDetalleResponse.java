package com.sicad.sicad_backend.dto.categoria;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDetalleResponse {
    private Integer idCategoria;
    private String nombre;
    private String descripcion;
    private boolean enabled;
}

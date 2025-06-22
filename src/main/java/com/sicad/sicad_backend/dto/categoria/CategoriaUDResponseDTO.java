package com.sicad.sicad_backend.dto.categoria;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaUDResponseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idCategoria;
    @NotBlank(message = "El nombre es un campo obligatorio")
    private String nombre;
}

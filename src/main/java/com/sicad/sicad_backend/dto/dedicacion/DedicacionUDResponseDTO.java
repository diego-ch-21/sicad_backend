package com.sicad.sicad_backend.dto.dedicacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DedicacionUDResponseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idDedicacion;
    @NotBlank(message = "nombre es un campo obligatorio")
    private String nombre;
}

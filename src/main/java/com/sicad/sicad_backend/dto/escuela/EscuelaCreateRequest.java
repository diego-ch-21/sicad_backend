package com.sicad.sicad_backend.dto.escuela;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaCreateRequest {
    @NotBlank(message = "El nombre es un campo obligatorio")
    private String nombre;
}

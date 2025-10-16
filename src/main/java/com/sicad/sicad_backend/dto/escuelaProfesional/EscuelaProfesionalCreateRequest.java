package com.sicad.sicad_backend.dto.escuelaProfesional;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaProfesionalCreateRequest {
    @NotBlank(message = "El email es un campo obligatorio")
    private String email;
    @NotBlank(message = "El password es un campo obligatorio")
    private String password;
    @NotBlank(message = "Los nombre es un campo obligatorio")
    private String nombre;
    @NotBlank(message = "Los apellido es un campo obligatorio")
    private String apellido;
}

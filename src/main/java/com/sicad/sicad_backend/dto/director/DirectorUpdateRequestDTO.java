package com.sicad.sicad_backend.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorUpdateRequestDTO {
    @NotBlank(message = "El email es un campo obligatorio")
    private String email;
    private String password;
    @NotBlank(message = "Los nombre es un campo obligatorio")
    private String nombre;
    @NotBlank(message = "Los apellido es un campo obligatorio")
    private String apellido;
    @NotNull(message = "El cargo es un campo obligatorio")
    private String cargo;
}

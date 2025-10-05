package com.sicad.sicad_backend.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorUpdateRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private Integer idEscuela;

}

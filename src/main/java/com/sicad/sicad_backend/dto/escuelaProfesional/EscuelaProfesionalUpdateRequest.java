package com.sicad.sicad_backend.dto.escuelaProfesional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaProfesionalUpdateRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
}

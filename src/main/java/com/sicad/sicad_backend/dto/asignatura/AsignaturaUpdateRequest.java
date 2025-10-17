package com.sicad.sicad_backend.dto.asignatura;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignaturaUpdateRequest {
    private String nombre;
    @Size(max = 8, message = "El código no debe exceder los 8 caracteres")
    private String codigo;
}

package com.sicad.sicad_backend.dto.asignatura;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignaturaCreateRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 8, message = "El código no debe exceder los 8 caracteres")
    private String codigo;
}

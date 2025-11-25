package com.sicad.sicad_backend.dto.asignatura;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder; // Importación necesaria
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder // Anotación agregada
public class AsignaturaCreateRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @Size(max = 8, message = "El código no debe exceder los 8 caracteres")
    private String codigo;
}

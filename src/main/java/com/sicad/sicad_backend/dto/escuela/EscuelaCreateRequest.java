package com.sicad.sicad_backend.dto.escuela;

import com.sicad.sicad_backend.dto.facultad.FacultadResumenResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaCreateRequest {
    @NotBlank(message = "El nombre es un campo obligatorio")
    private String nombre;
    @NotNull(message = "La facultad es un campo obligatorio")
    private Integer idFacultad;
}

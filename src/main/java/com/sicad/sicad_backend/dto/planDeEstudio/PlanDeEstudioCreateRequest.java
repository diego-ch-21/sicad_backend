package com.sicad.sicad_backend.dto.planDeEstudio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeEstudioCreateRequest {
    @NotNull(message = "la idFacultad es un campo obligatorio")
    private Integer idFacultad;
    @NotBlank(message = "El nombre es un campo obligatorio")
    private String nombre;
}

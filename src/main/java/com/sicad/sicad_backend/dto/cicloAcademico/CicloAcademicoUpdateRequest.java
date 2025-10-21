package com.sicad.sicad_backend.dto.cicloAcademico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloAcademicoUpdateRequest {

    @Min(value = 1000, message = "El año debe tener al menos 4 dígitos")
    private Integer anio;

    @Min(value = 0, message = "El periodo solo puede ser 0, 1 o 2")
    @Max(value = 2, message = "El periodo solo puede ser 0, 1 o 2")
    private Integer periodo;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "La fecha de inicio debe tener el formato yyyy-MM-dd (por ejemplo, 2025-03-27)"
    )
    private String fechaInicio;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "La fecha de fin debe tener el formato yyyy-MM-dd (por ejemplo, 2025-08-15)"
    )
    private String fechaFin;
}

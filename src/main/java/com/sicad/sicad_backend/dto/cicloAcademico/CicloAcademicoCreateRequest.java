package com.sicad.sicad_backend.dto.cicloAcademico;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloAcademicoCreateRequest {

    @NotNull(message = "El año es un campo obligatorio")
    @Min(value = 1000, message = "El año debe tener al menos 4 dígitos")
    private Integer anio;

    @NotNull(message = "El periodo es un campo obligatorio")
    @Min(value = 0, message = "El periodo solo puede ser 0, 1 o 2")
    @Max(value = 2, message = "El periodo solo puede ser 0, 1 o 2")
    private Integer periodo;

    @NotBlank(message = "La fecha de inicio es un campo obligatorio")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "La fecha de inicio debe tener el formato yyyy-MM-dd (por ejemplo, 2025-03-27)"
    )
    private String fechaInicio;

    @NotBlank(message = "La fecha de fin es un campo obligatorio")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "La fecha de fin debe tener el formato yyyy-MM-dd (por ejemplo, 2025-08-15)"
    )
    private String fechaFin;
}

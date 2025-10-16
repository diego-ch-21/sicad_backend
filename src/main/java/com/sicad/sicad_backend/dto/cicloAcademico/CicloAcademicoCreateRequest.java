package com.sicad.sicad_backend.dto.cicloAcademico;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloAcademicoCreateRequest {
    @NotNull(message = "El año es un campo obligatorio")
    private Integer anio;
    @NotNull(message = "El periodo es un campo obligatorio")
    private Integer periodo;
    @NotBlank(message = "La fecha de inicio es un campo obligatorio")
    @Pattern(
            regexp = "^\\d{2}-\\d{2}-\\d{4}$",
            message = "La fecha de inicio debe tener el formato dd-MM-yyyy"
    )
    private String fechaInicio;

    @NotBlank(message = "La fecha de fin es un campo obligatorio")
    @Pattern(
            regexp = "^\\d{2}-\\d{2}-\\d{4}$",
            message = "La fecha de fin debe tener el formato dd-MM-yyyy"
    )
    private String fechaFin;
}

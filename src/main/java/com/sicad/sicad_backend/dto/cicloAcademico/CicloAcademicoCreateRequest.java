package com.sicad.sicad_backend.dto.cicloAcademico;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "La FechaInicio de incio es un campo obligatorio")
    private String fechaInicio;
    @NotBlank(message = "La FechaFin de fin es un campo obligatorio")
    private String fechaFin;
}

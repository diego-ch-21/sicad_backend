package com.sicad.sicad_backend.dto.cicloAcademico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloAcademicoUpdateRequest {
    private Integer idCicloAcademico;
    private Integer anio;
    private Integer periodo;
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
}

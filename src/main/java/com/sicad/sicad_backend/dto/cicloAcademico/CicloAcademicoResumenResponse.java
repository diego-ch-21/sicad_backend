package com.sicad.sicad_backend.dto.cicloAcademico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloAcademicoResumenResponse {
    private Integer idCicloAcademico;
    private String nombre;
}

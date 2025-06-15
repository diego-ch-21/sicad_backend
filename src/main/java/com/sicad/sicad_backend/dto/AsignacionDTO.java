package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionDTO {
    private int idAsignacion;
    private int idDocente;
    private int idHorario;
    private String tipoAsignacion;
    private LocalDate fechaAsignacion;
}
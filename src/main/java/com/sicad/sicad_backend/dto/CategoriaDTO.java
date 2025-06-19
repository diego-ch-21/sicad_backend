package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {
    private String idCategoria;
    private String idDocente;
    private String idHorario;
    private String tipoAsignacion;
    private LocalDate fechaAsignacion;
    private boolean enabled;
}

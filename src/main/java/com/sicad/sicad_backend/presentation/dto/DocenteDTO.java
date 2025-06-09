package com.sicad.sicad_backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteDTO {
    private Integer idDocente;
    private String categoria;
    private Integer horasMaxLectivas;
    private boolean isPermiso;
}

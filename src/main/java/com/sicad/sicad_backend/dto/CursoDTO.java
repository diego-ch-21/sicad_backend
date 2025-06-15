package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoDTO {
    private Integer idCurso;
    private String codigo;
    private String nombre;
    private String ciclo;
    private String mallaCurricular;
}

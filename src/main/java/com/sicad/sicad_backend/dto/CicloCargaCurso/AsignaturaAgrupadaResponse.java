package com.sicad.sicad_backend.dto.CicloCargaCurso;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignaturaAgrupadaResponse {
    private String nombre;
    private String codigo;
    private List<CursoConDocenteResponse> cursos;
}

package com.sicad.sicad_backend.dto.CicloCargaCurso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoConDocenteResponse {
    private Integer idCurso;
    private String codigoCurso;
    private String grupo;
    private List<String> planDeEstudios;
    private String escuela;
    private Integer idDocente;
    private String docente;
    private List<HorarioCursoResponse> horarios;
}
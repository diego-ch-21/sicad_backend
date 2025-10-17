package com.sicad.sicad_backend.dto.curso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoResumenResponse {
    private Integer idCurso;
    private Integer idAsignatura;
    private List<String> planDeEstudios;
    private Integer idEscuela;
    private Integer idCicloAcademico;
    private String grupo;
    private boolean enabled;
}

package com.sicad.sicad_backend.dto.curso;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoUpdateRequest {
    private Integer idAsignatura;
    private Integer idPlanDeEstudio;
    private Integer idEscuela;
    private Integer idCicloAcademico;
    private String grupo;
}

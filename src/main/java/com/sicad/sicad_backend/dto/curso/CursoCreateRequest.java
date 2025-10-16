package com.sicad.sicad_backend.dto.curso;

import com.sicad.sicad_backend.dto.cursoHorario.HorarioCreateRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoCreateRequest {

    @NotNull(message = "El idAsignatura es un campo obligatorio")
    private Integer idAsignatura;

    @NotNull(message = "El idPlanDeEstudio es un campo obligatorio")
    private Integer idPlanDeEstudio;

    @NotNull(message = "El idEscuela es un campo obligatorio")
    private Integer idEscuela;

    @NotNull(message = "El idCicloAcademico es un campo obligatorio")
    private Integer idCicloAcademico;

    private String grupo=null;

    private List<HorarioCreateRequest> horario;

}
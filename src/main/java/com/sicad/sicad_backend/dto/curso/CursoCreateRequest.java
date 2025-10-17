package com.sicad.sicad_backend.dto.curso;

import com.sicad.sicad_backend.dto.Horario.HorarioCreateRequest;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoCreateRequest {

    @NotNull(message = "El idAsignatura es un campo obligatorio")
    private Integer idAsignatura;

    @NotEmpty(message = "Debe incluir al menos un plan de estudio")
    private List<String> planDeEstudios = new ArrayList<>();

    @NotNull(message = "El idEscuela es un campo obligatorio")
    private Integer idEscuela;

    @NotNull(message = "El idCicloAcademico es un campo obligatorio")
    private Integer idCicloAcademico;

    //opcional, si no lo introduce se genrara autmaticamente
    @Pattern(regexp = "^G[1-9][0-9]*$", message = "El grupo debe tener el formato G1, G2, G3, etc.")
    private String grupo;

    @NotNull(message = "El ciclo es obligatorio")
    @Min(value = 1, message = "El ciclo debe ser mínimo 1")
    @Max(value = 10, message = "El ciclo debe ser máximo 10")
    private Integer ciclo;

    private List<HorarioCreateRequest> horario;

}
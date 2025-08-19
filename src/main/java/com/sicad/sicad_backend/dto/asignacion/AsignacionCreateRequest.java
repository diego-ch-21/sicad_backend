package com.sicad.sicad_backend.dto.asignacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionCreateRequest {
    @NotNull(message = "El idDocente es un campo obligatorio")
    private Integer idDocente;
    @NotNull(message = "El idCurso es un campo obligatorio")
    private Integer idCurso;
    @NotNull(message = "El idCicloAcademico es un campo obligatorio")
    private Integer idCicloAcademico;
    @NotBlank(message = "El tipo de asignación es un campo obligatorio")
    private String tipoAsignacion;
}
package com.sicad.sicad_backend.dto.preferencia;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class   PreferenciaCreateRequest {
    @NotNull(message = "El idDocente es un campo obligatorio")
    private Integer idDocente;
    @NotNull(message = "El idAsignatura es un campo obligatorio")
    private Integer idAsignatura;
    @NotNull(message = "El idCicloAcademico es un campo obligatorio")
    private Integer idCicloAcademico;
    @NotNull(message = "El idEscuela es un campo obligatorio")
    private Integer idEscuela;
}

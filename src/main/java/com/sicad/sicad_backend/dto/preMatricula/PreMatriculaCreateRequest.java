package com.sicad.sicad_backend.dto.preMatricula;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreMatriculaCreateRequest {
    @NotNull(message = "la idAsignatura es un campo es obligatoria")
    private Integer idAsignatura;
    @NotNull(message = "la idCicloAcademico es un campo es obligatorio")
    private Integer idCicloAcademico;
    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad debe ser cero o un número positivo")
    private Integer cantidad;
}

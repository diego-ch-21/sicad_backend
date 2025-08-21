package com.sicad.sicad_backend.dto.preMatricula;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreMatriculaUpdateRequest {
    private Integer idAsignatura;
    private Integer idCicloAcademico;
    @PositiveOrZero(message = "La cantidad debe ser cero o un número positivo")
    private Integer cantidad;
}

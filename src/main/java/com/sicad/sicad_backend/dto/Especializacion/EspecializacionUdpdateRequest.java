package com.sicad.sicad_backend.dto.Especializacion;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecializacionUdpdateRequest {
    private Integer idAsignatura;
    private Integer idDocente;
}

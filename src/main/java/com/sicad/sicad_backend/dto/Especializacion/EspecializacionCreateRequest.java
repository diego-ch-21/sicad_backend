package com.sicad.sicad_backend.dto.Especializacion;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecializacionCreateRequest {
    @NotNull(message="asigantura es un campo obligatorio")
    private Integer idAsignatura;
    @NotNull(message="docente es un campo obligatorio")
    private Integer idDocente;
}

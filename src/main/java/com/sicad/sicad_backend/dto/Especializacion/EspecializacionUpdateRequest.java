package com.sicad.sicad_backend.dto.Especializacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecializacionUpdateRequest {
    private Integer idAsignatura;
    private Integer idDocente;
}

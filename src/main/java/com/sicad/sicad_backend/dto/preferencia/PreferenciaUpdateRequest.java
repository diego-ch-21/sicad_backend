package com.sicad.sicad_backend.dto.preferencia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaUpdateRequest {
    private Integer idDocente;
    private Integer idAsignatura;
    private Integer idCicloAcademico;
    private Integer idEscuela;
}

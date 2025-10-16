package com.sicad.sicad_backend.dto.escuelaProfesional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaProfesionalUsuarioResponse {
    private Integer idDepartamentoAcademico;
    private String codigo;
    private String descripcion;
    private boolean enabled;
}

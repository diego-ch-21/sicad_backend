package com.sicad.sicad_backend.dto.departamentoAcademico;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.escuela.EscuelaResumenResponse;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoAcademicoUsuarioResponse {
    private Integer idDepartamentoAcademico;
    private String codigo;
    private String descripcion;
    private boolean enabled;
}

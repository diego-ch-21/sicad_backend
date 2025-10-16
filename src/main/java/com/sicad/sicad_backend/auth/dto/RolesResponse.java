package com.sicad.sicad_backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.departamentoAcademico.DepartamentoAcademicoUsuarioResponse;
import com.sicad.sicad_backend.dto.docente.DocenteUsuarioResponse;
import com.sicad.sicad_backend.dto.escuelaProfesional.EscuelaProfesionalUsuarioResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUsuarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolesResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DocenteUsuarioResponse docente;
    private DepartamentoAcademicoUsuarioResponse departamentoAcademico;
    private LogisticaUsuarioResponse logistica;
    private EscuelaProfesionalUsuarioResponse escuelaProfesional;
}

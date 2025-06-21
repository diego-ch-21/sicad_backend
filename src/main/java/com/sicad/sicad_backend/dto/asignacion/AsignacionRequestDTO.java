package com.sicad.sicad_backend.dto.asignacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idAsignacion;
    private Integer idDocente;
    private Integer idHorario;
    private String tipoAsignacion;
    private LocalDate fechaAsignacion;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}
package com.sicad.sicad_backend.dto.horarioCurso;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioCursoRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idHorario;
    private Integer idCurso;
    private String grupo;
    private String tipoSesion;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private String aula;
    private Integer duracionHoras;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
}

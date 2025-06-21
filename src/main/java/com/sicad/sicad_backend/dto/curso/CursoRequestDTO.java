package com.sicad.sicad_backend.dto.curso;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoRequestDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idCurso;
    private String codigo;
    private String nombre;
    private String ciclo;
    private String mallaCurricular;
}

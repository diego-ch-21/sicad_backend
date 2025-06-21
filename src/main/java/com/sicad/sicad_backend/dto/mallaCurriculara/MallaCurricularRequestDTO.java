package com.sicad.sicad_backend.dto.mallaCurriculara;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MallaCurricularRequestDTO {
    private Integer idMallaCurricular;
    private String nombre;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled;
}

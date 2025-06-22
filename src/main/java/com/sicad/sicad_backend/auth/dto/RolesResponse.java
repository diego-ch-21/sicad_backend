package com.sicad.sicad_backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.director.DirectorUResponseDTO;
import com.sicad.sicad_backend.dto.docente.DocenteUResponseDTO;
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
    private DocenteUResponseDTO docente;
    private DirectorUResponseDTO director;
}

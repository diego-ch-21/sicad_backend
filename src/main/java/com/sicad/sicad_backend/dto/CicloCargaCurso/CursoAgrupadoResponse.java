package com.sicad.sicad_backend.dto.CicloCargaCurso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoAgrupadoResponse {
    private Integer ciclo;
    private List<AsignaturaAgrupadaResponse> asignaturas;
}
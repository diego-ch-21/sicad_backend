package com.sicad.sicad_backend.dto.carga;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.model.CicloAcademico;

import java.time.LocalDateTime;

public record CargaDetalleResponse(
        Integer idCarga,
        CicloAcademicoResumenResponse cicloAcademico,
        AlgoritmoDetalleResponse algoritmo,
        boolean principal,
        LocalDateTime createdAt,
        boolean enabled
) {
}

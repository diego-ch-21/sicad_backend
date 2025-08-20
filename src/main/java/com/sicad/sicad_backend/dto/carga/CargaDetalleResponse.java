package com.sicad.sicad_backend.dto.carga;

import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.model.CicloAcademico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaDetalleResponse{
    private Integer idCarga;
    private CicloAcademicoResumenResponse cicloAcademico;
    private AlgoritmoResumenResponse algoritmo;
    private boolean principal;
    private LocalDateTime createdAt;
    private boolean enabled;
}

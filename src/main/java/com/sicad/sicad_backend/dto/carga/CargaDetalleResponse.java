package com.sicad.sicad_backend.dto.carga;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoDetalleResponse;
import com.sicad.sicad_backend.dto.algoritmo.AlgoritmoResumenResponse;
import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.dto.resultado.ResultadoDetalleResponse;
import com.sicad.sicad_backend.model.CicloAcademico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CargaDetalleResponse{
    private Integer idCarga;
    private CicloAcademicoResumenResponse cicloAcademico;
    private AlgoritmoResumenResponse algoritmo;
    private ResultadoDetalleResponse resultado;
    private boolean principal;
    private LocalDateTime createdAt;
    private boolean enabled;
}

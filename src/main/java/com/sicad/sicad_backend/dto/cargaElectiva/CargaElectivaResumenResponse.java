package com.sicad.sicad_backend.dto.cargaElectiva;

import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaElectivaResumenResponse {
    private Integer idCargaElectiva;
    private String nombre;
}

package com.sicad.sicad_backend.dto.cargaElectiva;

import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.model.CicloAcademico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaElectivaDetalleResponse {
    private Integer idCargaElectiva;
    private CicloAcademicoResumenResponse cicloAcademico;
    private String nombre;
    private Boolean enabled;

}

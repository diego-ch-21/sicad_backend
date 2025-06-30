package com.sicad.sicad_backend.dto.disponibilidad;

import com.sicad.sicad_backend.dto.cargaElectiva.CargaElectivaResumenResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadDetalleResponse {
    private DocenteResumenResponse docente;
    private CargaElectivaResumenResponse cargaElectiva;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Boolean enabled;
}

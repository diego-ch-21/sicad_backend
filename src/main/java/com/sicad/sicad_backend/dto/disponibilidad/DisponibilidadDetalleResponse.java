package com.sicad.sicad_backend.dto.disponibilidad;

import com.sicad.sicad_backend.dto.cicloAcademico.CicloAcademicoResumenResponse;
import com.sicad.sicad_backend.dto.docente.DocenteResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadDetalleResponse {
    private Integer idDisponibilidad;
    private DocenteResumenResponse docente;
    private CicloAcademicoResumenResponse cicloAcademico;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Boolean enabled;
}

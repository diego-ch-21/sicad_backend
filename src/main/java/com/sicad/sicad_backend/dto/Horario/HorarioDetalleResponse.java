package com.sicad.sicad_backend.dto.Horario;

import com.sicad.sicad_backend.dto.aula.AulaResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDetalleResponse {
    private Integer idHorario;
    private String tipoSesion;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Integer duracionHoras;
    private AulaResumenResponse aula;
    private boolean enabled;
}

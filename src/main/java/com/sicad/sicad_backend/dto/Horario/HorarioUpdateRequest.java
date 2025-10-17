package com.sicad.sicad_backend.dto.Horario;

import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioUpdateRequest {
    private String tipoSesion;

    @Pattern(regexp = "^(lunes|martes|miercoles|jueves|viernes|sabado|domingo)$", message = "Día de la semana inválido")
    private String diaSemana;

    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "La hora debe estar en formato HH:mm:ss")
    private String horaInicio;

    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "La hora debe estar en formato HH:mm:ss")
    private String horaFin;

    @Min(value = 1, message = "La duración debe ser al menos de 1 hora")
    private Integer duracionHoras;

    private Integer idAula;
}

package com.sicad.sicad_backend.dto.disponibilidad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadCreateRequest {

    @NotNull(message = "El idDocente es un campo obligatorio")
    private Integer idDocente;

    @NotNull(message = "El idCicloAcademico es un campo obligatorio")
    private Integer idCicloAcademico;

    @NotBlank(message = "El día de la semana es obligatorio")
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", message = "Día de la semana inválido")
    private String diaSemana;

    @NotBlank(message = "La hora de inicio es obligatorio")
    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "La hora de inicio debe tener el formato HH:mm (00:00 a 23:59)"
    )
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatorio")
    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "La hora de fin debe tener el formato HH:mm (00:00 a 23:59)"
    )
    private String horaFin;
}

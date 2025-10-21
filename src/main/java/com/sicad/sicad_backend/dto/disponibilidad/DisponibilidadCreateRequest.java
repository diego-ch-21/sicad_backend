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
    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):00:00$", message = "La hora debe ser en punto y en formato HH:00:00")
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatorio")
    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):00:00$", message = "La hora debe ser en punto y en formato HH:00:00")
    private String horaFin;
}

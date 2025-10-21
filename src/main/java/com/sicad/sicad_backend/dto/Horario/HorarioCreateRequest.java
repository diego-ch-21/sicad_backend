package com.sicad.sicad_backend.dto.Horario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioCreateRequest {

    @NotBlank(message = "El tipo de sesión es obligatorio")
    @Pattern(regexp = "^(T|L|P|T-P)$", message = "Tipo de sesión inválido")
    private String tipoSesion;

    @NotBlank(message = "El día de la semana es obligatorio")
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", message = "Día de la semana inválido")
    private String diaSemana;

    @NotBlank(message = "La hora de inicio es obligatoria")
    // Validación estricta para "HH:00:00"
    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):00:00$", message = "La hora debe ser en punto y en formato HH:00:00")
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatoria")
    // Validación estricta para "HH:00:00"
    @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):00:00$", message = "La hora debe ser en punto y en formato HH:00:00")
    private String horaFin;

    @NotNull(message = "La duración es un campo obligatorio")
    @Min(value = 1, message = "La duración debe ser al menos de 1 hora")
    private Integer duracionHoras;

    private Integer idAula;
}
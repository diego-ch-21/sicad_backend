package com.sicad.sicad_backend.dto.cursoHorario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoHorarioCreateRequest {
    @NotBlank(message = "El tipo de sesión es obligatorio")
    private String tipoSesion;

    @NotBlank(message = "El día de la semana es obligatorio")
    @Pattern(regexp = "^(lunes|martes|miercoles|jueves|viernes|sabado|domingo)$", message = "Día de la semana inválido")
    private String diaSemana;

    @NotBlank(message = "La hora de inicio es obligatoria")
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "La hora debe estar en formato HH:mm:ss")
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatoria")
    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "La hora debe estar en formato HH:mm:ss")
    private String horaFin;

    @NotNull(message = "La duración es un campo obligatorio")
    @Min(value = 1, message = "La duración debe ser al menos de 1 hora")
    private Integer duracionHoras;
}

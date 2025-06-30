package com.sicad.sicad_backend.dto.curso;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CursoCreateRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idCurso;

    @NotNull(message = "El idAsignatura es un campo obligatorio")
    private Integer idAsignatura;

    @NotNull(message = "El idPlanDeEstudio es un campo obligatorio")
    private Integer idPlanDeEstudio;

    @NotNull(message = "El idEscuela es un campo obligatorio")
    private Integer idEscuela;

    @NotNull(message = "El idCicloAcademico es un campo obligatorio")
    private Integer idCicloAcademico;

    @NotBlank(message = "El grupo es un campo obligatorio")
    private String grupo;

    @NotBlank(message = "El tipo de sesión es obligatorio")
    private String tipoSesion;

    @NotBlank(message = "El día de la semana es obligatorio")
    @Pattern(regexp = "^(lunes|martes|miércoles|jueves|viernes|sábado|domingo)$", message = "Día de la semana inválido")
    private String diaSemana;

    @NotBlank(message = "La hora de inicio es obligatoria")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "La hora de inicio debe estar en formato HH:mm")
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatoria")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "La hora de fin debe estar en formato HH:mm")
    private String horaFin;

    @NotBlank(message = "El aula de inicio es obligatoria")
    private String aula;

    @NotNull(message = "La duración es un campo obligatorio")
    @Min(value = 1, message = "La duración debe ser al menos de 1 hora")
    private Integer duracionHoras;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer carga = null;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled = true;
}
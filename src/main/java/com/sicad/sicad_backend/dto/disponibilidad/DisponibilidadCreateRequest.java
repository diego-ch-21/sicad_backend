package com.sicad.sicad_backend.dto.disponibilidad;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadCreateRequest {

    @NotNull(message = "El idDocente es un campo obligatorio")
    private Integer idDocente;

    @NotNull(message = "El idCargaElectiva es un campo obligatorio")
    private Integer idCargaElectiva;

    @NotBlank(message = "El día de la semana es obligatorio")
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", message = "Día de la semana inválido")
    private String diaSemana;

    @NotBlank(message = "La hora de inicio es obligatoria")
    private String horaInicio;

    @NotBlank(message = "La hora de fin es obligatoria")
    private String horaFin;
}

package com.sicad.sicad_backend.dto.Aula;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AulaCreateRequest {

    @NotBlank(message = "El tipo de aula es obligatorio")
    @Pattern(regexp = "^(LABORATORIO|TEORIA)$", message = "El tipo debe ser LABORATORIO o TEORIA")
    private String tipo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El piso es obligatorio")
    @Min(value = 1, message = "El piso debe ser mínimo 1")
    @Max(value = 3, message = "El piso debe ser máximo 3")
    private Integer piso;

    @NotNull(message = "La capacidad es obligatoria")
    @PositiveOrZero(message = "La capacidad debe ser cero o un número positivo")
    private Integer capacidad;

    @PositiveOrZero(message = "El número de equipos debe ser cero o un número positivo")
    private Integer numeroEquipos;

    @NotBlank(message = "El (estado) es obligatorio")
    @Pattern(
            regexp = "^(DISPONIBLE|OCUPADO|MANTENIMIENTO)$",
            message = "El (estado) debe ser DISPONIBLE, OCUPADO o MANTENIMIENTO"
    )
    private String estado;

}

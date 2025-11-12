package com.sicad.sicad_backend.dto.aula;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AulaUpdateRequest {
    @Pattern(regexp = "^(LABORATORIO|TEORIA)$", message = "El tipo debe ser LABORATORIO o TEORIA")
    private String tipo;

    private String nombre;

    @Min(value = 1, message = "El piso debe ser mínimo 1")
    @Max(value = 3, message = "El piso debe ser máximo 3")
    private Integer piso;

    @PositiveOrZero(message = "La capacidad debe ser cero o un número positivo")
    private Integer capacidad;

    @PositiveOrZero(message = "El número de equipos debe ser cero o un número positivo")
    private Integer numeroEquipos;

    @Pattern(
            regexp = "^(DISPONIBLE|OCUPADO|MANTENIMIENTO)$",
            message = "El (estado) debe ser DISPONIBLE, OCUPADO o MANTENIMIENTO"
    )
    private String estado;
}

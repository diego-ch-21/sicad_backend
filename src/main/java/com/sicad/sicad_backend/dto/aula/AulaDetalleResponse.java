package com.sicad.sicad_backend.dto.aula;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AulaDetalleResponse {
    private Integer idAula;
    private String tipo;
    private String nombre;
    private Integer piso;
    private Integer capacidad;
    private Integer numeroEquipos;
    private String estado;
    private boolean enabled;
}

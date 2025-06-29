package com.sicad.sicad_backend.dto.asignatura;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignaturaDetalleResponse {
    private Integer idAsignatura;
    private String codigo;
    private String nombre;
    private Boolean enabled;
}

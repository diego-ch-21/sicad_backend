package com.sicad.sicad_backend.dto.dedicacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DedicacionUpdateRequest {
    private String nombre;
    private Integer horasTotales;
    private Integer horasMinLectivas;
    private Integer horasMaxLectivas;
}

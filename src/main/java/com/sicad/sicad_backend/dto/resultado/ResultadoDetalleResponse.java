package com.sicad.sicad_backend.dto.resultado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoDetalleResponse {
    private Integer idResultado;

    private Integer cursosAsignados;
    private Integer totalCursos;
    private Double porcentajeCursosAsignados;
    private Integer cursosSinAsignar;

    private Integer docentesUtilizados;
    private Integer totalDocentes;
    private Double porcentajeDocentesUtilizados;

    private Double porcentajePreferenciasSatisfechas;
    private Double promedioHoras;
    private Double maxHoras;
    private Double minHoras;

    private Integer docentesExcedidos;

    private Boolean enabled;
}

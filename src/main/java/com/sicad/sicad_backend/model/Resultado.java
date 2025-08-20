package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "resultado")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer idResultado;

    @Column(nullable = false, name = "cursos_asignados")
    private int cursosAsignados;

    @Column(nullable = false, name = "total_cursos")
    private int totalCursos;

    @Column(nullable = false, name = "porcentaje_cursos_asignados")
    private double porcentajeCursosAsignados;

    @Column(nullable = false, name = "cursos_sin_asignar")
    private int cursosSinAsignar;

    @Column(nullable = false, name = "docentes_utilizados")
    private int docentesUtilizados;

    @Column(nullable = false, name = "total_docentes")
    private int totalDocentes;

    @Column(nullable = false, name = "porcentaje_docentes_utilizados")
    private double porcentajeDocentesUtilizados;

    @Column(nullable = false, name = "porcentaje_preferencias_satisfechas")
    private double porcentajePreferenciasSatisfechas;

    @Column(nullable = false, name = "promedio_horas")
    private double promedioHoras;

    @Column(nullable = false, name = "max_horas")
    private int maxHoras;

    @Column(nullable = false, name = "min_horas")
    private int minHoras;

    @Column(nullable = false, name = "docentes_excedidos")
    private int docentesExcedidos;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}
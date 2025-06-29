package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="curso")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_curso")
    private Integer idCurso;

    @ManyToOne
    @JoinColumn(name = "id_asignatura", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_ASIGNATURA"))
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "id_plan_de_estudio", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_PLAN_DE_ESTUDIO"))
    private PlanDeEstudio planDeEstudio;

    @ManyToOne
    @JoinColumn(name = "id_escuela", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_ESCUELA"))
    private Escuela escuela;

    @ManyToOne
    @JoinColumn(name = "id_ciclo_academico", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_CICLO_ACADEMICO"))
    private CicloAcademico cicloAcademico;

    @Column(nullable = false, name = "grupo")
    private String grupo;

    @Column(nullable = false, name = "tipo_sesion")
    private String tipoSesion;

    @Column(nullable = false, name = "dia_semana")
    private String diaSemana;

    @Column(nullable = false, name = "hora_inicio")
    private String horaInicio;

    @Column(nullable = false, name = "hora_fin")
    private String horaFin;

    @Column(nullable = false, name = "aula")
    private String aula;

    @Column(nullable = false, name = "duracion_horas")
    private Integer duracionHoras;

    @Column(nullable = false, name = "carga")
    private Integer Carga;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

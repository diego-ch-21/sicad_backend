package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
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

    @Column(nullable = false, unique = true, name = "codigo")
    private String codigo;

    @Column(nullable = false, name = "grupo")
    private String grupo;

    @Column(nullable = false, name = "tipo_sesion")
    private String tipoSesion;

    @Column(nullable = false, name = "dia_semana")
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false, columnDefinition = "TIME(6)")
    private Time horaInicio;

    @Column(name = "hora_fin", nullable = false, columnDefinition = "TIME(6)")
    private Time horaFin;

    @Column(nullable = false, name = "aula")
    private String aula;

    @Column(nullable = false, name = "duracion_horas")
    private Integer duracionHoras;

    @Column(name = "carga")
    private Integer Carga;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

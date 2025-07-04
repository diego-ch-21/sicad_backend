package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "carga")
    private Integer Carga;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CursoHorario> cursoHorario = new ArrayList<>();

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

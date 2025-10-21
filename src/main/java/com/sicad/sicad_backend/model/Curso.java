package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

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

    @ElementCollection
    @CollectionTable(
            name = "curso_plan_estudios",
            joinColumns = @JoinColumn(name = "curso_id_curso")
    )
    @Column(name = "plan")
    private List<String> planDeEstudios = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_escuela", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_ESCUELA"))
    private Escuela escuela;

    @ManyToOne
    @JoinColumn(name = "id_ciclo_academico", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_CICLO_ACADEMICO"))
    private CicloAcademico cicloAcademico;

    @Column(nullable = true, unique = true, name = "codigo")
    private String codigo;

    @Column(nullable = false, name = "grupo")
    private String grupo;

    @Column(nullable = false, name = "ciclo")
    private Integer ciclo;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "enabled = true")
    private List<Horario> horarios = new ArrayList<>();


    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

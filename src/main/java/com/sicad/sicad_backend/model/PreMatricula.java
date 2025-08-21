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
@Table(
        name = "pre_matricula",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_PREMATRICULA_CICLO_ASIGNATURA",
                        columnNames = {"id_ciclo_academico", "id_curso"}
                )
        }
)
public class PreMatricula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pre_matricula")
    private Integer idPreMatricula;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_curso",
            foreignKey = @ForeignKey(name = "FK_PREMATRICULA_ASIGNATURA"))
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "id_ciclo_academico", nullable = false,
            foreignKey = @ForeignKey(name = "FK_PREMATRICULA_CICLO_ACADEMICO"))
    private CicloAcademico cicloAcademico;

    @Column(nullable = false, name = "cantidad")
    private Integer cantidad;

    @Column(nullable = false, name = "enabled")
    private boolean enabled;
}

package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="plan_curso")
public class PlanCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idPlanCurso;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_plan_de_estudio",
            foreignKey = @ForeignKey(name = "FK_plan_curso_plan_de_estudio"))
    private PlanDeEstudio planDeEstudio;
    @ManyToOne
    @JoinColumn(nullable = false, name = "id_curso",
            foreignKey = @ForeignKey(name = "FK_plan_curso_curso"))
    private Asignatura asignatura;

    @Column(nullable = false,name = "ciclo")
    @Min(1)
    @Max(10)
    private Integer ciclo;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

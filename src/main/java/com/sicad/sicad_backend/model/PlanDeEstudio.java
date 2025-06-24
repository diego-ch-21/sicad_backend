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
@Table(name="plan_de_estudio")
public class PlanDeEstudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_plan_de_estudio")
    private Integer idPlanDeEstudio;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_facultad", nullable = false,
            foreignKey = @ForeignKey(name = "FK_plan_de_estudio_facultad"))
    private Facultad facultad;

    @Column(nullable = false, name = "codigo")
    private Integer codigo;
    @Column(nullable = false, name = "nombre")
    private String nombre;
    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

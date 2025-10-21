package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="plan_de_estudio")
public class PlanDeEstudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_plan_de_estudio")
    private Integer idPlanDeEstudio;


    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

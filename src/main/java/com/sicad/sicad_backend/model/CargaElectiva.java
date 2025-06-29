package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="carga_electiva")
public class CargaElectiva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_carga_electiva")
    private Integer idCargaElectiva;

    @ManyToOne
    @JoinColumn(name = "id_ciclo_academico", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CURSO_CICLO_ACADEMICO"))
    private CicloAcademico cicloAcademico;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

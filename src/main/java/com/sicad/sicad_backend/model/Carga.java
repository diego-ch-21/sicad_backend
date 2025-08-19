package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "carga")
public class Carga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carga")
    @EqualsAndHashCode.Include
    private Integer idCarga;

    @ManyToOne
    @JoinColumn(name = "id_ciclo_academico", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CARGA_CICLO_ACADEMICO"))
    private CicloAcademico cicloAcademico;

    @Column(name = "codigo", nullable = false)
    private String cadigo;

    @ManyToOne
    @JoinColumn(name = "id_algoritmo", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CARGA_ALGORITMO"))
    private Algoritmo algoritmo;

    @Column(name= "principal")
    private boolean principal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}

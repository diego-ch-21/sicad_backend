package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aula")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aula")
    @EqualsAndHashCode.Include
    private Integer idAula;
    @Column(name = "tipo", nullable = false)
    private String tipo;
    @Column(name = "codigo", nullable = true)
    private String codigo;
    @Column(name = "piso", nullable = true)
    private Integer piso;
    @Column(name = "capacidad", nullable = true)
    private Integer capacidad;
    @Column(name = "estado", nullable = true)
    private String estado;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}

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
    @Column(name = "nombre", nullable = true)
    private String nombre;
    @Column(name = "piso", nullable = false)
    private Integer piso;
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;
    @Column(name = "numeroEquipos",nullable = true)
    private Integer numeroEquipos;
    @Column(name = "estado", nullable = false)
    private String estado;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}

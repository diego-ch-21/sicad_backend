package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="ciclo_academico")
public class CicloAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_ciclo_academico")
    private Integer idCicloAcademico;
    @Column(nullable = false, name = "anio")
    private Integer anio;
    @Column(nullable = false, name = "periodo")
    private Integer periodo;
    @Column(nullable = false, name = "nombre")
    private String nombre;
    @Column(nullable = false, name = "fecha_inicio")
    private LocalDate fechaInicio;
    @Column(nullable = false, name = "fecha_fin")
    private LocalDate fechaFin;
    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

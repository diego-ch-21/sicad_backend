package com.sicad.sicad_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="dedicacion")
public class Dedicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_dedicacion")
    private Integer idDedicacion;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = false, name = "horas_totales")
    private Integer horasTotales;

    @Column(nullable = false, name = "horas_lectivas_minima")
    private Integer horasMinLectivas;

    @Column(name = "hora_max_lectivas")
    private Integer horasMaxLectivas;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

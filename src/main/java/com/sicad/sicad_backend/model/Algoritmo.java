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
@Table(name = "algoritmo")
public class Algoritmo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_algoritmo")
    @EqualsAndHashCode.Include
    private Integer idAlgoritmo;

    @Column(nullable = false, name = "principal")
    private Boolean principal;

    @Column(name = "poblacion", nullable = true)
    private Integer poblacion;

    @Column(name = "generacion_ga", nullable = true)
    private Integer generacionGa;

    @Column(name = "prob_cruzamientos", nullable = true)
    private double probCruzamientos;

    @Column(name = "prob_mutacion", nullable = true)
    private double probMutacion;

    @Column(name = "elitismo", nullable = true)
    private double elitismo;

    @Column(name = "enjambre_pso", nullable = true)
    private Integer enjambrePso;

    @Column(name = "iteraciones_pso", nullable = true)
    private Integer iteracionesPso;

    @Column(name = "inercia_inicial", nullable = true)
    private double inerciaInicial;

    @Column(name = "inercia_final", nullable = true)
    private double inerciaFinal;

    @Column(name = "c_uno", nullable = true)
    private double cUno;

    @Column(name = "c_dos", nullable = true)
    private double cDos;

    @Column(name = "velocidad_maxima", nullable = true)
    private double velocidadMaxima;

    @Column(name = "ciclo_hibridos", nullable = true)
    private Integer cicloHibridos;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}

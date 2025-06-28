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
@Table(name="carga_electiva")
public class CargaElectiva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_carga_electiva")
    private Integer idCargaElectiva;

    @Column(nullable = false, name = "anio")
    private Integer anio;

    @Column(nullable = false, name = "periodo")
    private Integer periodo;

    @Column(nullable = false, name = "semestre_academico")
    private String semestreAcademico;

    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}

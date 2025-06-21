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
@Table(name="curso")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id_curso")
    private Integer idCurso;
    @Column(nullable = false, name = "codigo")
    private String codigo;
    @Column(nullable = false, name = "nombre")
    private String nombre;
    @Column(nullable = false, name = "ciclo")
    private String ciclo;
    @Column(nullable = false, name = "malla_curricular")
    private String mallaCurricular;
    @Column(nullable = false, name = "enabled")
    private Boolean enabled;
}
